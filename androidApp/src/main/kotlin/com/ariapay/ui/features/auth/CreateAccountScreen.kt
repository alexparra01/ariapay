package com.ariapay.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.components.ConfirmationBottomSheet
import com.ariapay.ui.components.ThemeButton
import com.ariapay.ui.components.TransparentTopBar
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme

data class CountryCode(
    val flag: String,
    val code: String,
    val dialCode: String,
    val minPhoneLength: Int,
    val maxPhoneLength: Int
)

val countryCodes = listOf(
    CountryCode("🇮🇩", "ID", "+62", 9, 12),   // Indonesia
    CountryCode("🇲🇾", "MY", "+60", 9, 10),   // Malaysia
    CountryCode("🇸🇬", "SG", "+65", 8, 8),    // Singapore
    CountryCode("🇹🇭", "TH", "+66", 9, 9),    // Thailand
    CountryCode("🇵🇭", "PH", "+63", 10, 10),  // Philippines
    CountryCode("🇻🇳", "VN", "+84", 9, 10),   // Vietnam
    CountryCode("🇦🇺", "AU", "+61", 9, 9),    // Australia
    CountryCode("🇺🇸", "US", "+1", 10, 10),   // United States
    CountryCode("🇬🇧", "GB", "+44", 10, 10),  // United Kingdom
    CountryCode("🇯🇵", "JP", "+81", 10, 10),  // Japan
)

@Composable
fun CreateAccountScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onCreateAccount: (phoneNumber: String, countryCode: String) -> Unit = { _, _ -> }
) {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countryCodes[0]) } // Default to Indonesia
    var expanded by remember { mutableStateOf(false) }
    var phoneNumberTouched by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Validation
    val isPhoneValid = phoneNumber.length in selectedCountry.minPhoneLength..selectedCountry.maxPhoneLength
    val showError = phoneNumberTouched && phoneNumber.isNotEmpty() && !isPhoneValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        // Transparent Top Bar
        TransparentTopBar(onBackClick = onNavigateBack)

        // Content with horizontal padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text("Let's get started", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter your phone number. We will send you a confirmation code there.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(48.dp))

            // Phone Number Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country Code Dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F3F3))
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedCountry.flag, fontSize = 24.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            selectedCountry.dialCode,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select country",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .heightIn(max = 300.dp)
                    ) {
                        countryCodes.forEach { country ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(country.flag, fontSize = 20.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "${country.dialCode} (${country.code})",
                                            fontSize = 14.sp
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCountry = country
                                    expanded = false
                                    // Clear phone number when changing country
                                    phoneNumber = ""
                                    phoneNumberTouched = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                TextField(
                    value = phoneNumber,
                    onValueChange = {
                        // Only accept digits and limit to max length
                        val filtered = it.filter { char -> char.isDigit() }
                            .take(selectedCountry.maxPhoneLength)
                        phoneNumber = filtered
                        if (!phoneNumberTouched) phoneNumberTouched = true
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            getPlaceholderForCountry(selectedCountry.code),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = showError,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = AriaPayColors.Primary,
                        unfocusedIndicatorColor = Color.Gray,
                        errorIndicatorColor = MaterialTheme.colorScheme.error
                    )
                )
            }

            // Error Message
            if (showError) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = getErrorMessageForCountry(selectedCountry),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Login Link - aligned to start
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.offset(x = (-12).dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    "Already have an account? Log in",
                    color = AriaPayColors.Primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))

            // Create Account Button
            ThemeButton(
                text = "Create Account",
                onClick = { showBottomSheet = true },
                enabled = isPhoneValid,
                backgroundColor = AriaPayColors.Primary,
                textColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
    if (showBottomSheet) {
        ConfirmationBottomSheet(
            phoneNumber = phoneNumber,
            countryCode = selectedCountry.dialCode,
            onConfirm = {
                showBottomSheet = false
                onCreateAccount(phoneNumber, selectedCountry.dialCode)
            },
            onGoBack = { showBottomSheet = false },
            onDismiss = { showBottomSheet = false }
        )
    }
}

private fun getPlaceholderForCountry(countryCode: String): String {
    return when (countryCode) {
        "ID" -> "812 3456 7890"
        "MY" -> "12 345 6789"
        "SG" -> "8123 4567"
        "TH" -> "81 234 5678"
        "PH" -> "912 345 6789"
        "VN" -> "91 234 5678"
        "AU" -> "412 345 678"
        "US" -> "201 555 0123"
        "GB" -> "7911 123456"
        "JP" -> "90 1234 5678"
        else -> "Phone number"
    }
}

private fun getErrorMessageForCountry(country: CountryCode): String {
    return if (country.minPhoneLength == country.maxPhoneLength) {
        "Phone number must be ${country.minPhoneLength} digits"
    } else {
        "Phone number must be ${country.minPhoneLength}-${country.maxPhoneLength} digits"
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountScreenPreview() {
    AriaPayTheme {
        CreateAccountScreen(
            onNavigateBack = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountScreenWithNumberPreview() {
    AriaPayTheme {
        CreateAccountScreen(
            onNavigateBack = {},
            onNavigateToLogin = {}
        )
    }
}