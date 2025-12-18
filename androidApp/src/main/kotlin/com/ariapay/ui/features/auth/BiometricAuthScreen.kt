package com.ariapay.ui.features.auth

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.biometrics.BiometricAuthManager
import com.ariapay.biometrics.BiometricAuthManager.BiometricResult

// AriaPay Brand Colors
private val AriaTeal = Color(0xFF0D9488)
private val AriaTealLight = Color(0xFF14B8A6)
private val AriaTealDark = Color(0xFF0F766E)
private val AriaCoralAccent = Color(0xFFF87171)
private val AriaBackground = Color(0xFFFAFAFA)

@Composable
fun BiometricAuthScreen(
    onBiometricEnabled: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Create the biometric manager
    val biometricManager = remember { BiometricAuthManager(context) }

    // UI State
    var isAuthenticating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var biometricAvailable by remember { mutableStateOf(true) }

    // Check availability on launch
    LaunchedEffect(Unit) {
        val availability = biometricManager.isBiometricAvailable()
        biometricAvailable = availability == BiometricAuthManager.BiometricAvailability.AVAILABLE
    }

    // Collect authentication results
    LaunchedEffect(Unit) {
        biometricManager.promptResult.collect { result ->
            isAuthenticating = false
            when (result) {
                is BiometricResult.Success -> {
                    errorMessage = null
                    onBiometricEnabled()
                }
                is BiometricResult.Error -> {
                    errorMessage = result.message
                }
                is BiometricResult.Failed -> {
                    // Single failure, user can retry - don't show error
                }
                is BiometricResult.Cancelled -> {
                    errorMessage = null // User cancelled, no error
                }
                is BiometricResult.HardwareUnavailable -> {
                    errorMessage = "Biometric hardware unavailable"
                    biometricAvailable = false
                }
                is BiometricResult.FeatureUnavailable -> {
                    errorMessage = "Biometric feature not supported"
                    biometricAvailable = false
                }
                is BiometricResult.NoneEnrolled -> {
                    errorMessage = "No biometrics enrolled. Please set up in device settings"
                    biometricAvailable = false
                }
            }
        }
    }

    // Cancel authentication on dispose
    DisposableEffect(Unit) {
        onDispose {
            biometricManager.cancelAuthentication()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AriaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 120.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Animated Biometric Icon
            BiometricIconAnimation(isAuthenticating = isAuthenticating)

            Spacer(modifier = Modifier.height(48.dp))

            // Title
            Text(
                text = "Log in with a look",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Use Face ID instead of a passcode to log in. It's more secure",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Error message
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = AriaCoralAccent,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Enable Biometric Button
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        isAuthenticating = true
                        errorMessage = null
                        biometricManager.showBiometricPrompt(
                            title = "AriaPay Authentication",
                            subtitle = "Verify your identity to enable biometric login",
                            description = "Touch the fingerprint sensor or look at the camera",
                            negativeButtonText = "Cancel"
                        )
                    } else {
                        errorMessage = "Biometrics require Android 9 (API 28) or higher"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AriaTeal
                ),
                enabled = biometricAvailable && !isAuthenticating
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Enable Face ID",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Not available message
            if (!biometricAvailable) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Biometric authentication not available",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSkip) {
                Text(
                    text = "Skip for now",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun BiometricIconAnimation(
    isAuthenticating: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")

    // Scanning line animation
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanProgress"
    )

    // Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(200.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Corner brackets (scanner frame)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bracketLength = 40.dp.toPx()
            val bracketOffset = 20.dp.toPx()
            val strokeWidth = 4.dp.toPx()

            // Top-left corner
            drawLine(
                color = AriaCoralAccent,
                start = Offset(bracketOffset, bracketOffset + bracketLength),
                end = Offset(bracketOffset, bracketOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = AriaCoralAccent,
                start = Offset(bracketOffset, bracketOffset),
                end = Offset(bracketOffset + bracketLength, bracketOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Top-right corner
            drawLine(
                color = AriaCoralAccent,
                start = Offset(size.width - bracketOffset - bracketLength, bracketOffset),
                end = Offset(size.width - bracketOffset, bracketOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = AriaCoralAccent,
                start = Offset(size.width - bracketOffset, bracketOffset),
                end = Offset(size.width - bracketOffset, bracketOffset + bracketLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom-left corner
            drawLine(
                color = AriaCoralAccent,
                start = Offset(bracketOffset, size.height - bracketOffset - bracketLength),
                end = Offset(bracketOffset, size.height - bracketOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = AriaCoralAccent,
                start = Offset(bracketOffset, size.height - bracketOffset),
                end = Offset(bracketOffset + bracketLength, size.height - bracketOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Bottom-right corner
            drawLine(
                color = AriaCoralAccent,
                start = Offset(size.width - bracketOffset - bracketLength, size.height - bracketOffset),
                end = Offset(size.width - bracketOffset, size.height - bracketOffset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = AriaCoralAccent,
                start = Offset(size.width - bracketOffset, size.height - bracketOffset),
                end = Offset(size.width - bracketOffset, size.height - bracketOffset - bracketLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Center face icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AriaTealLight, AriaTeal)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            FaceIcon()
        }

        // Scanning line overlay when authenticating
        if (isAuthenticating) {
            Canvas(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            ) {
                val lineY = size.height * scanProgress
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun FaceIcon() {
    Canvas(modifier = Modifier.size(60.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val faceColor = Color(0xFF0F766E)

        // Left eye
        drawCircle(
            color = faceColor,
            radius = 5.dp.toPx(),
            center = Offset(centerX - 12.dp.toPx(), centerY - 8.dp.toPx())
        )

        // Right eye
        drawCircle(
            color = faceColor,
            radius = 5.dp.toPx(),
            center = Offset(centerX + 12.dp.toPx(), centerY - 8.dp.toPx())
        )

        // Smile
        val smilePath = Path().apply {
            moveTo(centerX - 15.dp.toPx(), centerY + 8.dp.toPx())
            quadraticTo(
                centerX, centerY + 22.dp.toPx(),
                centerX + 15.dp.toPx(), centerY + 8.dp.toPx()
            )
        }
        drawPath(
            path = smilePath,
            color = faceColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BiometricAuthScreenPreview() {
    BiometricAuthScreen(
        onBiometricEnabled = {},
        onSkip = {}
    )
}