package com.ariapay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    otpLength: Int = 6,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val errorColor = MaterialTheme.colorScheme.error

    // Only show error styling when OTP is complete AND there's an error
    val showError = isError && otpValue.length == otpLength

    // Request focus when the composable is first displayed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(modifier = modifier) {
        // Hidden text field that handles actual input
        BasicTextField(
            value = otpValue,
            onValueChange = { newValue ->
                // Only accept digits and limit to otpLength
                val filtered = newValue.filter { it.isDigit() }.take(otpLength)
                onOtpChange(filtered)
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent), // Hide cursor
            textStyle = TextStyle(color = Color.Transparent), // Hide text in the field itself
            decorationBox = { _ ->
                // Visual OTP boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(otpLength) { index ->
                        val char = otpValue.getOrNull(index)?.toString() ?: ""
                        val isFocused = otpValue.length == index

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (showError) Color(0xFFFEF2F2) else Color(0xFFF5F5F5))
                                .then(
                                    when {
                                        showError -> Modifier.border(
                                            width = 2.dp,
                                            color = errorColor,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        isFocused -> Modifier.border(
                                            width = 2.dp,
                                            color = AriaPayColors.Primary,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        else -> Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        )

        // Clickable overlay to ensure taps trigger focus
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpInputFieldPreview() {
    AriaPayTheme {
        OtpInputField(
            otpValue = "123",
            onOtpChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpInputFieldEmptyPreview() {
    AriaPayTheme {
        OtpInputField(
            otpValue = "",
            onOtpChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpInputFieldFullPreview() {
    AriaPayTheme {
        OtpInputField(
            otpValue = "123456",
            onOtpChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpInputFieldErrorPreview() {
    AriaPayTheme {
        OtpInputField(
            otpValue = "123456",
            onOtpChange = {},
            isError = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpInputFieldErrorButIncompletePreview() {
    AriaPayTheme {
        // This should NOT show error styling since OTP is incomplete
        OtpInputField(
            otpValue = "123",
            onOtpChange = {},
            isError = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}