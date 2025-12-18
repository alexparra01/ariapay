package com.ariapay.ui.features.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.R
import com.ariapay.ui.components.ThemeButton
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme

@Composable
fun OnboardScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToCreateAccount: () -> Unit
) {
    val topColor = AriaPayColors.Primary
    val bottomColor = Color(0xFFE29794)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(topColor)
            .drawBehind {
                val brush = Brush.radialGradient(
                    colors = listOf(bottomColor, Color.Transparent),
                    center = Offset(x = 0f, y = size.height),
                    radius = size.width * 1.3f
                )
                drawRect(brush = brush)
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(start = 24.dp, end = 24.dp, bottom = 48.dp, top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Welcome to AriaPay
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Welcome to ", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                Image(
                    painter = painterResource(id = R.drawable.ariapay),
                    contentDescription = "AriaPay Logo",
                    modifier = Modifier.height(20.dp)
                )
            }

            Spacer(Modifier.height(48.dp))

            // Title and Subtitle
            Text(
                text = "Easy Payments, Anywhere",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Store your cards securely and enjoy smooth transactions everywhere",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            // Main Image
            Image(
                painter = painterResource(id = R.drawable.wallet2),
                contentDescription = "Wallet with floating cards",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Image takes up available space
            )

            // Action Buttons
            ThemeButton(
                text = "Create Account",
                onClick = onNavigateToCreateAccount,
                backgroundColor = AriaPayColors.Primary,
                textColor = Color.White
            )

            Spacer(Modifier.height(16.dp))

            ThemeButton(
                text = "Login",
                onClick = onNavigateToLogin,
                backgroundColor = Color.White,
                textColor = AriaPayColors.Primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardScreenPreview() {
    AriaPayTheme {
        OnboardScreen(
            onNavigateToLogin = {},
            onNavigateToCreateAccount = {}
        )
    }
}
