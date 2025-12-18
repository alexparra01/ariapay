package com.ariapay.ui.features.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.components.NfcPulseAnimation
import com.ariapay.ui.components.PaymentCardView
import com.ariapay.ui.theme.AriaPayColors
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.MaterialTheme

@Composable
fun PaymentScreen(onPaymentComplete: (Boolean, String) -> Unit, onNavigateBack: () -> Unit, viewModel: PaymentViewModel = koinViewModel()) {
    val paymentState by viewModel.paymentState.collectAsState()
    val selectedCard by viewModel.selectedCard.collectAsState()
    var amount by remember { mutableStateOf("25.99") }
    
    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is PaymentState.Success -> onPaymentComplete(true, state.result.transactionId ?: "Success")
            is PaymentState.Failed -> onPaymentComplete(false, state.message)
            else -> {}
        }
    }
    
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) { Text("←", fontSize = 24.sp) }
            Text("NFC Payment", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(Modifier.height(32.dp))
        
        when (paymentState) {
            is PaymentState.Idle -> {
                selectedCard?.let { PaymentCardView(it, Modifier.padding(horizontal = 16.dp)) }
                Spacer(Modifier.height(32.dp))
                
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Demo Payment", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(amount, { amount = it }, Modifier.fillMaxWidth(), label = { Text("Amount (USD)") }, shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Merchant: Demo Store", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Spacer(Modifier.weight(1f))
                Text("Hold your phone near the terminal\nor tap the button below", textAlign = TextAlign.Center, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))
                
                Button({ amount.toDoubleOrNull()?.let { viewModel.processPayment(it, "merchant_demo", "Demo Store") } },
                    Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AriaPayColors.Primary)) {
                    Text("📱 Simulate NFC Payment", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            is PaymentState.Processing -> {
                Spacer(Modifier.weight(1f))
                NfcPulseAnimation()
                Spacer(Modifier.height(32.dp))
                Text("Processing payment...", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
            }
            else -> {}
        }
    }
}