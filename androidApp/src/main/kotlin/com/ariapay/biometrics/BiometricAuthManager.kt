package com.ariapay.biometrics

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * AriaPay Biometric Authentication Manager
 *
 * Uses the native android.hardware.biometrics API (API 28+) which does NOT
 * require FragmentActivity - works perfectly with pure Jetpack Compose!
 *
 * Usage in Composable:
 * ```kotlin
 * val context = LocalContext.current
 * val biometricManager = remember { BiometricAuthManager(context) }
 *
 * LaunchedEffect(Unit) {
 *     biometricManager.promptResult.collect { result ->
 *         when (result) {
 *             is BiometricResult.Success -> // Handle success
 *             is BiometricResult.Error -> // Handle error
 *             // etc.
 *         }
 *     }
 * }
 *
 * Button(onClick = { biometricManager.showBiometricPrompt(...) }) {
 *     Text("Authenticate")
 * }
 * ```
 */
class BiometricAuthManager(private val context: Context) {

    private var cancellationSignal: CancellationSignal? = null

    // Channel for emitting results
    private val resultChannel = Channel<BiometricResult>(Channel.BUFFERED)
    val promptResult: Flow<BiometricResult> = resultChannel.receiveAsFlow()

    /**
     * Biometric authentication result types
     */
    sealed interface BiometricResult {
        data object Success : BiometricResult
        data class Error(val errorCode: Int, val message: String) : BiometricResult
        data object Failed : BiometricResult
        data object Cancelled : BiometricResult
        data object HardwareUnavailable : BiometricResult
        data object FeatureUnavailable : BiometricResult
        data object NoneEnrolled : BiometricResult
    }

    /**
     * Check if biometric authentication is available on this device
     */
    fun isBiometricAvailable(): BiometricAvailability {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return BiometricAvailability.NOT_SUPPORTED
        }

        val biometricManager = context.getSystemService(BiometricManager::class.java)

        return when (biometricManager?.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SECURITY_UPDATE_REQUIRED
            else -> BiometricAvailability.NOT_SUPPORTED
        }
    }

    enum class BiometricAvailability {
        AVAILABLE,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NONE_ENROLLED,
        SECURITY_UPDATE_REQUIRED,
        NOT_SUPPORTED
    }

    /**
     * Show the biometric authentication prompt
     *
     * @param title Title shown on the prompt
     * @param subtitle Subtitle shown on the prompt
     * @param description Description text
     * @param negativeButtonText Text for the cancel/negative button
     * @param allowDeviceCredential Whether to allow PIN/Pattern/Password fallback (API 30+)
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun showBiometricPrompt(
        title: String = "AriaPay Authentication",
        subtitle: String = "Verify your identity",
        description: String = "Use biometric authentication to continue",
        negativeButtonText: String = "Cancel",
        allowDeviceCredential: Boolean = false
    ) {
        // Check availability first
        val availability = isBiometricAvailable()
        if (availability != BiometricAvailability.AVAILABLE) {
            val result = when (availability) {
                BiometricAvailability.NO_HARDWARE -> BiometricResult.HardwareUnavailable
                BiometricAvailability.HARDWARE_UNAVAILABLE -> BiometricResult.HardwareUnavailable
                BiometricAvailability.NONE_ENROLLED -> BiometricResult.NoneEnrolled
                else -> BiometricResult.FeatureUnavailable
            }
            resultChannel.trySend(result)
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        // Cancel any existing authentication
        cancellationSignal?.cancel()
        cancellationSignal = CancellationSignal()

        val callback = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                resultChannel.trySend(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val result = when (errorCode) {
                    BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED,
                    BiometricPrompt.BIOMETRIC_ERROR_CANCELED -> BiometricResult.Cancelled
                    else -> BiometricResult.Error(errorCode, errString.toString())
                }
                resultChannel.trySend(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                resultChannel.trySend(BiometricResult.Failed)
            }
        }

        val promptInfoBuilder = BiometricPrompt.Builder(context)
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)

        // API 30+ supports device credential fallback
        if (allowDeviceCredential && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfoBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            promptInfoBuilder.setNegativeButton(
                negativeButtonText,
                executor
            ) { _, _ ->
                resultChannel.trySend(BiometricResult.Cancelled)
            }
        }

        val biometricPrompt = promptInfoBuilder.build()

        biometricPrompt.authenticate(
            cancellationSignal!!,
            executor,
            callback
        )
    }

    /**
     * Cancel any ongoing biometric authentication
     */
    fun cancelAuthentication() {
        cancellationSignal?.cancel()
        cancellationSignal = null
    }

    companion object {
        // Common error codes for reference
        const val ERROR_HW_UNAVAILABLE = BiometricPrompt.BIOMETRIC_ERROR_HW_UNAVAILABLE
        const val ERROR_UNABLE_TO_PROCESS = BiometricPrompt.BIOMETRIC_ERROR_UNABLE_TO_PROCESS
        const val ERROR_TIMEOUT = BiometricPrompt.BIOMETRIC_ERROR_TIMEOUT
        const val ERROR_NO_SPACE = BiometricPrompt.BIOMETRIC_ERROR_NO_SPACE
        const val ERROR_CANCELED = BiometricPrompt.BIOMETRIC_ERROR_CANCELED
        const val ERROR_LOCKOUT = BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT
        const val ERROR_LOCKOUT_PERMANENT = BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT_PERMANENT
        const val ERROR_USER_CANCELED = BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED
        const val ERROR_NO_BIOMETRICS = BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS
        const val ERROR_HW_NOT_PRESENT = BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT
        const val ERROR_NO_DEVICE_CREDENTIAL = BiometricPrompt.BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL
    }
}