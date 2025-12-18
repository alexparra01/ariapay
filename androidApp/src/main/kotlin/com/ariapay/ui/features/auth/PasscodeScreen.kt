package com.ariapay.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme
import kotlinx.coroutines.delay
import com.ariapay.ui.components.NumberKeypad

enum class PasscodeStep {
    CREATE,
    CONFIRM
}

@Composable
fun PasscodeScreen(
    onClose: () -> Unit,
    onPasscodeCreated: (String) -> Unit
) {
    var passcode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var currentStep by remember { mutableStateOf(PasscodeStep.CREATE) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val currentPasscode = if (currentStep == PasscodeStep.CREATE) passcode else confirmPasscode
    val passcodeLength = 6

    // Handle passcode completion
    LaunchedEffect(passcode) {
        if (passcode.length == passcodeLength && currentStep == PasscodeStep.CREATE) {
            delay(200) // Small delay for visual feedback
            currentStep = PasscodeStep.CONFIRM
        }
    }

    LaunchedEffect(confirmPasscode) {
        if (confirmPasscode.length == passcodeLength && currentStep == PasscodeStep.CONFIRM) {
            delay(200)
            if (confirmPasscode == passcode) {
                onPasscodeCreated(passcode)
            } else {
                showError = true
                errorMessage = "Passcodes don't match. Try again."
                confirmPasscode = ""
            }
        }
    }

    // Clear error after delay
    LaunchedEffect(showError) {
        if (showError) {
            delay(2000)
            showError = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Close button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Title
        Text(
            text = if (currentStep == PasscodeStep.CREATE) "Create Passcode" else "Confirm Passcode",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Passcode Dots
        PasscodeDots(
            length = passcodeLength,
            filledCount = currentPasscode.length,
            isError = showError
        )

        // Error Message
        if (showError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Number Keypad
        NumberKeypad(
            onNumberClick = { number ->
                if (currentStep == PasscodeStep.CREATE) {
                    if (passcode.length < passcodeLength) {
                        passcode += number
                    }
                } else {
                    if (confirmPasscode.length < passcodeLength) {
                        confirmPasscode += number
                    }
                }
            },
            onDeleteClick = {
                if (currentStep == PasscodeStep.CREATE) {
                    if (passcode.isNotEmpty()) {
                        passcode = passcode.dropLast(1)
                    }
                } else {
                    if (confirmPasscode.isNotEmpty()) {
                        confirmPasscode = confirmPasscode.dropLast(1)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PasscodeDots(
    length: Int,
    filledCount: Int,
    isError: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(length) { index ->
            val isFilled = index < filledCount
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isError -> MaterialTheme.colorScheme.error
                            isFilled -> AriaPayColors.Primary
                            else -> Color(0xFFE0E0E0)
                        }
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasscodeScreenCreatePreview() {
    AriaPayTheme {
        PasscodeScreen(
            onClose = {},
            onPasscodeCreated = {}
        )
    }
}