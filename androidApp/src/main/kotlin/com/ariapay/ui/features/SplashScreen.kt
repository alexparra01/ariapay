package com.ariapay.ui.features

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.R
import com.ariapay.ui.features.auth.AuthenticationViewModel
import com.ariapay.ui.models.AuthState
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.theme.AriaPayTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthenticationViewModel = koinViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        delay(1500)
        when (authState) {
            is AuthState.Authenticated -> onNavigateToHome()
            is AuthState.Unauthenticated -> onNavigateToOnBoarding()
            else -> {}
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
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
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                modifier = Modifier.size(80.dp),
                contentDescription = "AriaPay Logo"
            )
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.ariapay),
                modifier = Modifier.height(36.dp),
                contentDescription = "AriaPay"
            )
            Spacer(Modifier.height(8.dp))
            Text("Tap. Pay. Go.", color = Color.White.copy(0.8f), fontSize = 18.sp)
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    AriaPayTheme {
        SplashContent()
    }
}
