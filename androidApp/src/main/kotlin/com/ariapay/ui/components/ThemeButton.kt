package com.ariapay.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme

@Composable
fun ThemeButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    border: BorderStroke? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.4f),
            disabledContentColor = textColor.copy(alpha = 0.7f)
        ),
        border = border
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) textColor else textColor.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeButtonEnabledPreview() {
    AriaPayTheme {
        ThemeButton(
            text = "Continue",
            onClick = {},
            backgroundColor = AriaPayColors.Primary,
            textColor = Color.White,
            enabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeButtonDisabledPreview() {
    AriaPayTheme {
        ThemeButton(
            text = "Continue",
            onClick = {},
            backgroundColor = AriaPayColors.Primary,
            textColor = Color.White,
            enabled = false
        )
    }
}