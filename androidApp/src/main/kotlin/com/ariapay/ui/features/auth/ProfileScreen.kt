package com.ariapay.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.components.ThemeButton
import com.ariapay.ui.components.TransparentTopBar
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onContinue: (firstName: String, lastName: String, email: String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Track if fields have been touched (for showing errors only after interaction)
    var firstNameTouched by remember { mutableStateOf(false) }
    var lastNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Validation logic
    val firstNameError = firstNameTouched && firstName.isNotEmpty() && firstName.length < 3
    val lastNameError = lastNameTouched && lastName.isNotEmpty() && lastName.length < 3
    val emailError = emailTouched && email.isNotEmpty() && !isValidEmail(email)

    // Check if all fields are valid and filled
    val isFormValid = firstName.length >= 3 &&
            lastName.length >= 3 &&
            isValidEmail(email)

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
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Your profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tell us a bit about yourself to finish setting up your Ariapay account",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // First Name Field
            ProfileTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    if (!firstNameTouched) firstNameTouched = true
                },
                placeholder = "First name",
                isError = firstNameError,
                errorMessage = "First name must be at least 3 characters",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(16.dp))

            // Last Name Field
            ProfileTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    if (!lastNameTouched) lastNameTouched = true
                },
                placeholder = "Last name",
                isError = lastNameError,
                errorMessage = "Last name must be at least 3 characters",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(16.dp))

            // Email Field
            ProfileTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (!emailTouched) emailTouched = true
                },
                placeholder = "Email",
                isError = emailError,
                errorMessage = "Invalid email address",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (isFormValid) {
                            onContinue(firstName, lastName, email)
                        }
                    }
                )
            )

            Spacer(Modifier.weight(1f))

            // Continue Button
            ThemeButton(
                text = "Continue",
                onClick = { onContinue(firstName, lastName, email) },
                enabled = isFormValid,
                backgroundColor = AriaPayColors.Primary,
                textColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    errorMessage: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = isError,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                errorContainerColor = Color(0xFFFEF2F2),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )

        if (isError) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(email)
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    AriaPayTheme {
        ProfileScreen(
            onNavigateBack = {},
            onContinue = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenFilledPreview() {
    AriaPayTheme {
        ProfileScreen(
            onNavigateBack = {},
            onContinue = { _, _, _ -> }
        )
    }
}