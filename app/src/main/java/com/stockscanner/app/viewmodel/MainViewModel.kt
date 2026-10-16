package com.stockscanner.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stockscanner.app.data.local.AppDataStore
import com.stockscanner.app.data.local.ExcelReader
import com.stockscanner.app.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ScannerState {
    object Idle     : ScannerState()
    object Scanning : ScannerState()
    data class Found(val product: Product) : ScannerState()
    object NotFound : ScannerState()
}

sealed class ExcelState {
    object None    : ExcelState()
    object Loading : ExcelState()
    data class Ready(val count: Int) : ExcelState()
    data class Error(val message: String) : ExcelState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = AppDataStore(application)

    val businessName: StateFlow<String?> = dataStore.businessName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val savedExcelPath: StateFlow<String?> = dataStore.excelPath
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _products     = MutableStateFlow<List<Product>>(emptyList())
    private val _excelState   = MutableStateFlow<ExcelState>(ExcelState.None)
    val excelState: StateFlow<ExcelState> = _excelState.asStateFlow()

    private val _scannerState = MutableStateFlow<ScannerState>(ScannerState.Idle)
    val scannerState: StateFlow<ScannerState> = _scannerState.asStateFlow()

    private val _snackbar = MutableSharedFlow<String>()
    val snackbar = _snackbar.asSharedFlow()

    private val _showFilePicker = MutableStateFlow(false)
    val showFilePicker: StateFlow<Boolean> = _showFilePicker.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.excelPath.firstOrNull()?.let { path ->
                if (path.isNotBlank()) loadFromPath(path)
            }
        }
    }

    fun saveBusinessName(name: String) = viewModelScope.launch {
        dataStore.saveBusinessName(name)
    }

    fun onLoadExcelPressed() {
        val path = savedExcelPath.value
        if (!path.isNullOrBlank()) loadFromPath(path)
        else _showFilePicker.value = true
    }

    fun onFilePickerResult(uri: Uri) {
        _showFilePicker.value = false
        _excelState.value = ExcelState.Loading
        viewModelScope.launch {
            try {
                val path = withContext(Dispatchers.IO) {
                    ExcelReader.copyToInternal(getApplication(), uri)
                }
                dataStore.saveExcelPath(path)
                loadFromPath(path)
            } catch (e: Exception) {
                _excelState.value = ExcelState.Error("No se pudo copiar el archivo: ${e.message}")
            }
        }
    }

    fun dismissFilePicker() { _showFilePicker.value = false }

    fun onChangeFilePressed() { _showFilePicker.value = true }

    private fun loadFromPath(path: String) {
        _excelState.value = ExcelState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { ExcelReader.readProducts(path) }
            result.fold(
                onSuccess = { list ->
                    _products.value = list
                    _excelState.value = ExcelState.Ready(list.size)
                },
                onFailure = { e ->
                    _excelState.value = ExcelState.Error(e.message ?: "Error al leer el archivo")
                    launch { _snackbar.emit("Error al leer el archivo Excel") }
                }
            )
        }
    }

    fun startScanning() { _scannerState.value = ScannerState.Scanning }

    fun onBarcodeDetected(barcode: String) {
        if (_scannerState.value !is ScannerState.Scanning) return
        val product = _products.value.find { it.barcode == barcode.trim() }
        _scannerState.value = if (product != null) ScannerState.Found(product)
                              else ScannerState.NotFound
    }

    fun retryAfterNotFound() { _scannerState.value = ScannerState.Scanning }

    fun resetScanner() { _scannerState.value = ScannerState.Idle }
}
