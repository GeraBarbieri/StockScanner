package com.stockscanner.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stockscanner.app.data.model.Product
import com.stockscanner.app.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductResultCard(product: Product) {
    val currencyFmt = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier            = Modifier.padding(20.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text  = "✓  Producto encontrado",
                style = MaterialTheme.typography.labelLarge,
                color = SuccessGreen
            )
            OutlinedTextField(
                value         = product.description,
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Descripción") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                minLines      = 2
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = product.stock.toString(),
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Stock") },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value         = currencyFmt.format(product.costWithoutVat),
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Costo s/IVA") },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
