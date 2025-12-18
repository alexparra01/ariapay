package com.ariapay.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ariapay.R
import com.ariapay.ui.theme.AriaPayColors

@Composable
fun NumberKeypad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeypadButton(text = "1", onClick = { onNumberClick("1") })
            KeypadButton(text = "2", onClick = { onNumberClick("2") })
            KeypadButton(text = "3", onClick = { onNumberClick("3") })
        }

        // Row 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeypadButton(text = "4", onClick = { onNumberClick("4") })
            KeypadButton(text = "5", onClick = { onNumberClick("5") })
            KeypadButton(text = "6", onClick = { onNumberClick("6") })
        }

        // Row 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KeypadButton(text = "7", onClick = { onNumberClick("7") })
            KeypadButton(text = "8", onClick = { onNumberClick("8") })
            KeypadButton(text = "9", onClick = { onNumberClick("9") })
        }

        // Row 4: empty, 0, delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Empty space
            Spacer(modifier = Modifier.size(72.dp))

            KeypadButton(text = "0", onClick = { onNumberClick("0") })

            // Delete button
            KeypadDeleteButton(onClick = onDeleteClick)
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = AriaPayColors.BackgroundDark
        )
    }
}

@Composable
private fun KeypadDeleteButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_backspace_24),
            contentDescription = "Delete",
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NumberKeypadPreview() {
    NumberKeypad(onNumberClick = {}, onDeleteClick = {})
}