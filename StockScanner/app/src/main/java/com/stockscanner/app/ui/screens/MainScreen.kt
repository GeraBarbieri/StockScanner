package com.stockscanner.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.stockscanner.app.ui.components.AppButton
import com.stockscanner.app.ui.components.AppOutlinedButton
import com.stockscanner.app.ui.components.BarcodeScannerView
import com.stockscanner.app.ui.components.ProductResultCard
import com.stockscanner.app.ui.theme.ErrorRed
import com.stockscanner.app.ui.theme.SuccessGreen
import com.stockscanner.app.ui.theme.TextSecondary
import com.stockscanner.app.viewmodel.ExcelState
import com.stockscanner.app.viewmodel.MainViewModel
import com.stockscanner.app.viewmodel.ScannerState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalGetImage::class)
@Composable
fun MainScreen(viewModel: MainViewModel, businessName: String) {
    val scannerState     by viewModel.scannerState.collectAsState()
    val excelState       by viewModel.excelState.collectAsState()
    val showPicker       by viewModel.showFilePicker.collectAsState()
    val snackbarHost     = remember { SnackbarHostState() }
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) viewModel.onFilePickerResult(uri)
        else viewModel.dismissFilePicker()
    }

    LaunchedEffect(showPicker) {
        if (showPicker) {
            filePickerLauncher.launch(
                arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-excel"
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.snackbar.collect { msg -> snackbarHost.showSnackbar(msg) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Nombre del comercio ─────────────────────────────────────────
            Text(
                text  = businessName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))

            // ── Sección Excel ───────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Archivo de productos", style = MaterialTheme.typography.titleSmall, color = TextSecondary)

                when (val state = excelState) {
                    is ExcelState.None -> {
                        AppButton(
                            text    = "Buscar archivo stock.xlsx",
                            onClick = { viewModel.onLoadExcelPressed() },
                            icon    = { Icon(Icons.Default.FolderOpen, null) }
                        )
                    }
                    is ExcelState.Loading -> {
                        AppButton(
                            text    = "Cargando archivo…",
                            onClick = {},
                            enabled = false,
                            icon    = { CircularProgressIndicator(
                                modifier    = Modifier.size(18.dp),
                                color       = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )}
                        )
                    }
                    is ExcelState.Ready -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors   = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("✓  Archivo cargado", style = MaterialTheme.typography.labelLarge, color = SuccessGreen)
                                    Text("${state.count} productos disponibles", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                IconButton(onClick = { viewModel.onChangeFilePressed() }) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = "Cambiar archivo", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                    is ExcelState.Error -> {
                        Text("⚠  ${state.message}", style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                        AppButton(
                            text    = "Intentar con otro archivo",
                            onClick = { viewModel.onChangeFilePressed() },
                            icon    = { Icon(Icons.Default.FolderOpen, null) }
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))

            // ── Sección Escáner ─────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Escáner", style = MaterialTheme.typography.titleSmall, color = TextSecondary)

                val excelReady = excelState is ExcelState.Ready
                AppButton(
                    text    = "Escanear código de barras",
                    enabled = excelReady,
                    onClick = {
                        if (cameraPermission.status.isGranted) viewModel.startScanning()
                        else cameraPermission.launchPermissionRequest()
                    },
                    icon    = { Icon(Icons.Default.QrCodeScanner, null) }
                )
                if (!excelReady) {
                    Text(
                        "Cargá un archivo Excel primero para habilitar el escáner.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // ── Resultado ───────────────────────────────────────────────────
            if (scannerState is ScannerState.Found) {
                ProductResultCard(product = (scannerState as ScannerState.Found).product)
                AppOutlinedButton(
                    text    = "Escanear otro producto",
                    onClick = { viewModel.startScanning() },
                    icon    = { Icon(Icons.Default.QrCodeScanner, null) }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // ── Cámara a pantalla completa ────────────────────────────────────────────
    if (scannerState is ScannerState.Scanning && cameraPermission.status.isGranted) {
        Dialog(
            onDismissRequest = { viewModel.resetScanner() },
            properties       = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                BarcodeScannerView(
                    onBarcodeDetected = { viewModel.onBarcodeDetected(it) },
                    modifier          = Modifier.fillMaxSize()
                )
                // Marco de escaneo
                Box(
                    modifier = Modifier.align(Alignment.Center).size(260.dp, 160.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokePx = 6f
                        val corner   = 40f
                        val paint    = Paint().apply {
                            color       = Color.White
                            strokeWidth = strokePx
                            style       = PaintingStyle.Stroke
                        }
                        drawContext.canvas.apply {
                            drawLine(Offset(0f, corner), Offset(0f, 0f), paint)
                            drawLine(Offset(0f, 0f), Offset(corner, 0f), paint)
                            drawLine(Offset(size.width - corner, 0f), Offset(size.width, 0f), paint)
                            drawLine(Offset(size.width, 0f), Offset(size.width, corner), paint)
                            drawLine(Offset(0f, size.height - corner), Offset(0f, size.height), paint)
                            drawLine(Offset(0f, size.height), Offset(corner, size.height), paint)
                            drawLine(Offset(size.width - corner, size.height), Offset(size.width, size.height), paint)
                            drawLine(Offset(size.width, size.height - corner), Offset(size.width, size.height), paint)
                        }
                    }
                }
                Text(
                    text     = "Apuntá al código de barras",
                    style    = MaterialTheme.typography.labelLarge,
                    color    = Color.White,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)
                )
                TextButton(
                    onClick  = { viewModel.resetScanner() },
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                ) {
                    Text("✕  Cancelar", color = Color.White)
                }
            }
        }
    }

    // ── Producto no encontrado ────────────────────────────────────────────────
    if (scannerState is ScannerState.NotFound) {
        AlertDialog(
            onDismissRequest = { viewModel.retryAfterNotFound() },
            title            = { Text("Producto no encontrado") },
            text             = { Text("El código escaneado no existe en el archivo cargado.") },
            confirmButton    = {
                TextButton(onClick = { viewModel.retryAfterNotFound() }) {
                    Text("Cerrar y volver a escanear")
                }
            }
        )
    }
}
