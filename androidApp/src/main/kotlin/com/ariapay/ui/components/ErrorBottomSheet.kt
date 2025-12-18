package com.ariapay.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.R
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    title: String = "Something went wrong",
    message: String = "Please try again later",
    buttonText: String = "Got it"
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Box(modifier = Modifier.size(width = 32.dp, height = 4.dp))
                }
            }
        ) {
            ErrorBottomSheetContent(
                title = title,
                message = message,
                buttonText = buttonText,
                onButtonClick = onDismiss
            )
        }
    }
}

@Composable
private fun ErrorBottomSheetContent(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Error Icon
        Image(
            painter = painterResource(id = R.drawable.errorpopupicon),
            contentDescription = "Error",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = AriaPayColors.Primary
            )
        ) {
            Text(
                text = buttonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorBottomSheetContentPreview() {
    AriaPayTheme {
        Surface {
            ErrorBottomSheetContent(
                title = "Incorrect Code Entered",
                message = "Please check the code and try again",
                buttonText = "Got it",
                onButtonClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorBottomSheetContentDefaultPreview() {
    AriaPayTheme {
        Surface {
            ErrorBottomSheetContent(
                title = "Something went wrong",
                message = "Please try again later",
                buttonText = "Got it",
                onButtonClick = {}
            )
        }
    }
}