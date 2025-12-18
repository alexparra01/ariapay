package com.ariapay.ui.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.models.LoginState
import com.ariapay.ui.theme.AriaPayColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: AuthenticationViewModel = koinViewModel()) {
    val loginState by viewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("demo@ariapay.com") }
    var password by remember { mutableStateOf("password123") }
    
    LaunchedEffect(loginState) { if (loginState is LoginState.Success) onLoginSuccess() }
    
    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), 
        horizontalAlignment = Alignment.CenterHorizontally, 
        verticalArrangement = Arrangement.Center
    ) {
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