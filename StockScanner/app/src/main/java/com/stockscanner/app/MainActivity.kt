package com.stockscanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.stockscanner.app.ui.screens.MainScreen
import com.stockscanner.app.ui.screens.SetupScreen
import com.stockscanner.app.ui.theme.StockScannerTheme
import com.stockscanner.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockScannerTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val businessName by viewModel.businessName.collectAsState()

                    when {
                        businessName == null      -> { /* Cargando DataStore — breve pausa */ }
                        businessName!!.isBlank()  -> SetupScreen { viewModel.saveBusinessName(it) }
                        else                      -> MainScreen(viewModel, businessName!!)
                    }
                }
            }
        }
    }
}
