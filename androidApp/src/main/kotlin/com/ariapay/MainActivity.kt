package com.ariapay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ariapay.ui.navigation.AriaPayNavHost
import com.ariapay.ui.theme.AriaPayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AriaPayTheme {
                AriaPayNavHost()
            }
        }
    }
}
