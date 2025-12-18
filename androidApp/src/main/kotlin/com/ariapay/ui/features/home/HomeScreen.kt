package com.ariapay.ui.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.components.PaymentCardView
import com.ariapay.ui.components.TransactionItem
import com.ariapay.ui.theme.AriaPayColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(onNavigateToPayment: () -> Unit, onNavigateToHistory: () -> Unit, onNavigateToSettings: () -> Unit, viewModel: HomeViewModel = koinViewModel()) {
    val walletState by viewModel.walletState.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("AriaPay", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AriaPayColors.Primary)
                Text("Ready to pay", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onNavigateToSettings) { Text("⚙️", fontSize = 24.sp) }
        }
        
        Spacer(Modifier.height(24.dp))
        
        when (val state = walletState) {
            is WalletState.Loading -> Card(Modifier.fillMaxWidth().height(200.dp), shape = RoundedCornerShape(16.dp)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is WalletState.Success -> {
                val card = state.wallet.cards.find { it.isDefault } ?: state.wallet.cards.firstOrNull()
                card?.let { PaymentCardView(it) }
            }
            is WalletState.Error -> Text(state.message, color = AriaPayColors.Error)
        }
        
        Spacer(Modifier.height(24.dp))
        Button(onNavigateToPayment, Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AriaPayColors.Secondary)) {
            Text("📱 Tap to Pay", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onNavigateToHistory) { Text("See All") }
        }
        
        Spacer(Modifier.height(8.dp))
        if (recentTransactions.isEmpty()) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No transactions yet.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recentTransactions.take(5)) { TransactionItem(it) }
            }
        }
    }
}