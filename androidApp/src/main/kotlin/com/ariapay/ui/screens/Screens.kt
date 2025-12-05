package com.ariapay.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.components.*
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.viewmodel.*
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit, onNavigateToHome: () -> Unit, viewModel: AuthViewModel = koinViewModel()) {
    val authState by viewModel.authState.collectAsState()
    val scale = remember { Animatable(0.5f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
        delay(1500)
    }
    
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> onNavigateToHome()
            is AuthState.Unauthenticated -> onNavigateToLogin()
            else -> {}
        }
    }
    
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(Modifier.fillMaxSize(), color = AriaPayColors.Primary) {
            Column(Modifier.scale(scale.value), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("💳", fontSize = 80.sp)
                Spacer(Modifier.height(24.dp))
                Text("AriaPay", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Text("Tap. Pay. Go.", color = Color.White.copy(0.8f), fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: AuthViewModel = koinViewModel()) {
    val loginState by viewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("demo@ariapay.com") }
    var password by remember { mutableStateOf("password123") }
    
    LaunchedEffect(loginState) { if (loginState is LoginState.Success) onLoginSuccess() }
    
    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), 
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("💳", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("AriaPay", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AriaPayColors.Primary)
        Text("Welcome back", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(48.dp))
        
        OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") }, 
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Password") }, 
            singleLine = true, visualTransformation = PasswordVisualTransformation(), shape = RoundedCornerShape(12.dp))
        
        if (loginState is LoginState.Error) {
            Spacer(Modifier.height(16.dp))
            Text((loginState as LoginState.Error).message, color = AriaPayColors.Error, fontSize = 14.sp)
        }
        
        Spacer(Modifier.height(32.dp))
        Button({ viewModel.login(email, password) }, Modifier.fillMaxWidth().height(56.dp), 
            enabled = loginState !is LoginState.Loading && email.isNotBlank() && password.isNotBlank(),
            shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = AriaPayColors.Primary)) {
            if (loginState is LoginState.Loading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            else Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(Modifier.height(24.dp))
        Surface(shape = RoundedCornerShape(8.dp), color = AriaPayColors.Secondary.copy(0.1f)) {
            Text("Demo: Use pre-filled credentials", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
                fontSize = 12.sp, color = AriaPayColors.Secondary)
        }
    }
}

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

@Composable
fun TransactionHistoryScreen(onNavigateBack: () -> Unit, viewModel: TransactionHistoryViewModel = koinViewModel()) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) { Text("←", fontSize = 24.sp) }
            Text("Transaction History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.refresh() }) { Text("🔄", fontSize = 20.sp) }
        }
        Spacer(Modifier.height(16.dp))
        
        if (isLoading && transactions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
        } else if (transactions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No transactions found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions) { TransactionItem(it) }
            }
        }
    }
}

@Composable
fun SettingsScreen(onNavigateBack: () -> Unit, onLogout: () -> Unit, viewModel: AuthViewModel = koinViewModel()) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) { Text("←", fontSize = 24.sp) }
            Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))
        
        listOf("👤" to "Profile", "🔔" to "Notifications", "🔒" to "Security", "❓" to "Help & Support").forEach { (icon, title) ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 24.sp)
                    Spacer(Modifier.width(16.dp))
                    Text(title, Modifier.weight(1f), fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Text("→", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        
        Spacer(Modifier.weight(1f))
        Button({ viewModel.logout(); onLogout() }, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AriaPayColors.Error)) {
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Text("AriaPay v1.0.0", Modifier.align(Alignment.CenterHorizontally), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
