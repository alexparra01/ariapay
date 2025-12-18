package com.ariapay.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationBottomSheet(
    phoneNumber: String,
    countryCode: String,
    onConfirm: () -> Unit,
    onGoBack: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phone number display
            Text("🇮🇩 $countryCode $phoneNumber", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            Spacer(Modifier.height(12.dp))
            
            // Subtitle
            Text(
                text = "Is it number correct? We\'ll send you a confirmation code there",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Confirm Button
            ThemeButton(
                text = "Confirm", 
                onClick = onConfirm, 
                backgroundColor = AriaPayColors.Primary, 
                textColor = Color.White
            )
            
            Spacer(Modifier.height(12.dp))

            // Go Back Button
            ThemeButton(
                text = "Go Back", 
                onClick = onGoBack, 
                backgroundColor = Color.White, 
                textColor = AriaPayColors.Primary,
                border = BorderStroke(1.dp, AriaPayColors.Primary) // Correctly apply the border
            )
            
            Spacer(Modifier.height(32.dp)) // Padding for the bottom
        }
    }
}

@Preview
@Composable
fun ConfirmationBottomSheetPreview() {
    AriaPayTheme {
        // Note: Previews for ModalBottomSheets may not appear as modals
        // in the design view, but will show the content correctly.
        ConfirmationBottomSheet(
            phoneNumber = "81234567890",
            countryCode = "+62",
            onConfirm = {},
            onGoBack = {},
            onDismiss = {}
        )
    }
}
