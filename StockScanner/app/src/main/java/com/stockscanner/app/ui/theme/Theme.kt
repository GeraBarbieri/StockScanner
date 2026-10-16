package com.stockscanner.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Terracotta     = Color(0xFFBF6242)
val TerracottaDark = Color(0xFF8C3E1F)
val Background     = Color(0xFFF5F0EA)
val Surface        = Color(0xFFFFFFFF)
val OnTerracotta   = Color(0xFFFFFFFF)
val TextPrimary    = Color(0xFF2C1A0E)
val TextSecondary  = Color(0xFF7A5C4A)
val ErrorRed       = Color(0xFFB94040)
val SuccessGreen   = Color(0xFF4A7C59)

private val ColorScheme = lightColorScheme(
    primary          = Terracotta,
    onPrimary        = OnTerracotta,
    primaryContainer = Color(0xFFE8C4B0),
    background       = Background,
    surface          = Surface,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    error            = ErrorRed,
)

@Composable
fun StockScannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
