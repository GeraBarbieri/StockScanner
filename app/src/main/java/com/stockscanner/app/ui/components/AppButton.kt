package com.stockscanner.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        shape     = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation  = 6.dp,
            pressedElevation  = 2.dp,
            disabledElevation = 0.dp
        ),
        modifier  = modifier.fillMaxWidth().height(54.dp)
    ) {
        if (icon != null) { icon(); Spacer(Modifier.width(8.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        onClick  = onClick,
        shape    = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth().height(54.dp)
    ) {
        if (icon != null) { icon(); Spacer(Modifier.width(8.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
