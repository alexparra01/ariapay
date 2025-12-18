package com.ariapay.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.R

@Composable
fun TransparentTopBar(
    onBackClick: (() -> Unit)? = null,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_arrow_back_ios_new_24),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Title (centered)
            if (title != null) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Actions slot
            Row(
                horizontalArrangement = Arrangement.End,
                content = actions
            )

            // Balance spacer if no actions
            if (actions == {}) {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}

