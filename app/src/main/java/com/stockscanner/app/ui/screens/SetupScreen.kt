package com.stockscanner.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stockscanner.app.ui.components.AppButton

@Composable
fun SetupScreen(onNameSaved: (String) -> Unit) {
    var name     by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier            = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = "👋 Bienvenido",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text      = "Ingresá el nombre de tu comercio para comenzar.",
            style     = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )
        Spacer(Modifier.height(36.dp))
        OutlinedTextField(
            value          = name,
            onValueChange  = { name = it; hasError = false },
            label          = { Text("Nombre del Comercio") },
            isError        = hasError,
            supportingText = { if (hasError) Text("El nombre no puede estar vacío") },
            singleLine     = true,
            shape          = RoundedCornerShape(14.dp),
            modifier       = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        AppButton(
            text    = "Guardar y continuar",
            onClick = { if (name.isBlank()) hasError = true else onNameSaved(name.trim()) }
        )
    }
}
