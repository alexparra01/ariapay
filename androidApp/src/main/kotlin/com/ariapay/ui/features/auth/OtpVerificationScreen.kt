package com.ariapay.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.components.ErrorBottomSheet
import com.ariapay.ui.components.OtpInputField
import com.ariapay.ui.components.TransparentTopBar
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    errorMessage: String? = null,
    onNavigateBack: () -> Unit,
    onVerifyOtp: (String) -> Unit,
    onResendCode: () -> Unit = {}
) {
    var otpValue by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(15) }

    // Track if we should show the error (only after OTP was complete and error received)
    var showError by remember { mutableStateOf(false) }
    var showBottomSheetError by remember { mutableStateOf(false) }


    //TODO remove this validation
    // Hide error when user starts typing again
    LaunchedEffect(otpValue) {
        if (otpValue.length == 6 && otpValue == "111111") {
            showError = false
        }
    }
    //TODO remove this validation
    LaunchedEffect(otpValue) {
        if (otpValue.length == 6 && otpValue == "000000") {
            showError = true
        }
        if (otpValue.length == 6 && otpValue == "222222") {
           showBottomSheetError = true
        }
    }


    // Countdown timer logic
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else {
            onResendCode()
        }
    }

    // Auto-submit when OTP is complete
    LaunchedEffect(otpValue) {
        if (otpValue.length == 6 && !showError && !showBottomSheetError) { // include this in the future errorMessage == null
            onVerifyOtp(otpValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        TransparentTopBar(onBackClick = onNavigateBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(24.dp))

            Text("6-digit code", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter the code sent to +62 ••••••• ${phoneNumber.takeLast(4)}",
                fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(48.dp))

            // OTP Input Field
            OtpInputField(
                otpValue = otpValue,
                onOtpChange = { otpValue = it },
                isError = showError && errorMessage != null
            )

            // Error Message - only show when OTP is complete and there's an error
            if (showError && errorMessage != null && otpValue.length == 6) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(24.dp))

            // Resend Code Timer
            Text(
                text = if (timeLeft > 0) "Resend code in 00:${
                    timeLeft.toString().padStart(2, '0')
                }" else "Resend code",
                color = if (timeLeft > 0) MaterialTheme.colorScheme.onSurfaceVariant else AriaPayColors.Primary,
                fontWeight = FontWeight.SemiBold

            )
        }
    }
    ErrorBottomSheet(
        title = "Something went wrong",
        message = "Please try again later",
        buttonText = "Got it",
        isVisible = showBottomSheetError,
        onDismiss = { showBottomSheetError = false }
    )
}

@Preview(showBackground = true)
@Composable
fun OtpVerificationScreenPreview() {
    AriaPayTheme {
        OtpVerificationScreen(
            phoneNumber = "81234567890",
            onNavigateBack = {},
            onVerifyOtp = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpVerificationScreenErrorPreview() {
    AriaPayTheme {
        OtpVerificationScreen(
            phoneNumber = "81234567890",
            errorMessage = "Invalid code. Please try again.",
            onNavigateBack = {},
            onVerifyOtp = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpVerificationScreenExpiredPreview() {
    AriaPayTheme {
        OtpVerificationScreen(
            phoneNumber = "81234567890",
            errorMessage = "Code expired. Please request a new one.",
            onNavigateBack = {},
            onVerifyOtp = {}
        )
    }
}