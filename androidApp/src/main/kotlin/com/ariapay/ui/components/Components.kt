package com.ariapay.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.domain.model.*
import com.ariapay.ui.theme.AriaPayColors

@Composable
fun PaymentCardView(card: PaymentCard, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Card(
        modifier = modifier.fillMaxWidth().height(200.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.linearGradient(listOf(AriaPayColors.Primary, AriaPayColors.PrimaryLight)))
                .padding(24.dp)
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("AriaPay", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    if (card.isDefault) {
                        Surface(shape = RoundedCornerShape(12.dp), color = AriaPayColors.Secondary) {
                            Text("DEFAULT", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), 
                                color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("•••• •••• •••• ${card.lastFourDigits}", color = Color.White.copy(0.9f), 
                    fontSize = 22.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("CARD HOLDER", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Text(card.cardholderName, color = Color.White, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("EXPIRES", color = Color.White.copy(0.6f), fontSize = 10.sp)
                        Text("${card.expiryMonth.toString().padStart(2, '0')}/${card.expiryYear % 100}", 
                            color = Color.White, fontSize = 14.sp)
                    }
                    Text(card.cardType.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(modifier = modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(Modifier.size(48.dp), shape = CircleShape,
                    color = when (transaction.status) {
                        TransactionStatus.COMPLETED -> AriaPayColors.Success.copy(0.1f)
                        TransactionStatus.FAILED -> AriaPayColors.Error.copy(0.1f)
                        else -> AriaPayColors.Warning.copy(0.1f)
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(when (transaction.status) {
                            TransactionStatus.COMPLETED -> "✓"
                            TransactionStatus.FAILED -> "✗"
                            else -> "⏳"
                        }, fontSize = 20.sp, color = when (transaction.status) {
                            TransactionStatus.COMPLETED -> AriaPayColors.Success
                            TransactionStatus.FAILED -> AriaPayColors.Error
                            else -> AriaPayColors.Warning
                        })
                    }
                }
                Column {
                    Text(transaction.merchantName, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Text("•••• ${transaction.cardLastFour}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
            Text("${transaction.currency} ${"%.2f".format(transaction.amount)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun NfcPulseAnimation(modifier: Modifier = Modifier, isAnimating: Boolean = true) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc")
    
    Box(modifier.size(200.dp), contentAlignment = Alignment.Center) {
        if (isAnimating) {
            repeat(3) { index ->
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.5f, targetValue = 1.5f,
                    animationSpec = infiniteRepeatable(tween(1000, delayMillis = index * 333, easing = EaseOut), RepeatMode.Restart),
                    label = "scale_$index"
                )
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.6f, targetValue = 0f,
                    animationSpec = infiniteRepeatable(tween(1000, delayMillis = index * 333, easing = EaseOut), RepeatMode.Restart),
                    label = "alpha_$index"
                )
                Box(Modifier.size(120.dp).scale(scale).clip(CircleShape).background(AriaPayColors.Secondary.copy(alpha)))
            }
        }
        Surface(Modifier.size(80.dp), shape = CircleShape, color = AriaPayColors.Primary) {
            Box(contentAlignment = Alignment.Center) { Text("📱", fontSize = 32.sp) }
        }
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier, message: String = "Loading...") {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(color = AriaPayColors.Primary)
        Spacer(Modifier.height(16.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
