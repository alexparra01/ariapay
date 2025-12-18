package com.ariapay.ui.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.ui.theme.AriaPayColors
import com.ariapay.ui.features.auth.AuthenticationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(onNavigateBack: () -> Unit, onLogout: () -> Unit, viewModel: AuthenticationViewModel = koinViewModel()) {
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