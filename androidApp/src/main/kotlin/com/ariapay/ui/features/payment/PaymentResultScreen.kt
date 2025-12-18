package com.ariapay.ui.features.payment

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.theme.AriaPayColors

@Composable
fun PaymentResultScreen(success: Boolean, message: String, onNavigateToHome: () -> Unit) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) { scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
    
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Surface(Modifier.size(120.dp).scale(scale.value), shape = CircleShape, 
            color = if (success) AriaPayColors.Success else AriaPayColors.Error) {
            Box(contentAlignment = Alignment.Center) { Text(if (success) "✓" else "✗", fontSize = 64.sp, color = Color.White) }
        }
        Spacer(Modifier.height(32.dp))
        Text(if (success) "Payment Successful!" else "Payment Failed", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = if (success) AriaPayColors.Success else AriaPayColors.Error)
        Spacer(Modifier.height(8.dp))
        Text(message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(48.dp))
        Button(onNavigateToHome, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AriaPayColors.Primary)) {
            Text("Back to Home", fontWeight = FontWeight.Bold)
        }
    }
}