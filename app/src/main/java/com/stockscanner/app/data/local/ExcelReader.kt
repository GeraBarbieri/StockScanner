package com.stockscanner.app.data.local

import android.content.Context
import android.net.Uri
import com.stockscanner.app.data.model.Product
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object ExcelReader {

    fun copyToInternal(context: Context, uri: Uri, fileName: String = "stock.xlsx"): String {
        val dest = File(context.filesDir, fileName)
        context.contentResolver.openInputStream(uri)!!.use { input ->
            FileOutputStream(dest).use { output -> input.copyTo(output) }
        }
        return dest.absolutePath
    }

    fun readProducts(path: String): Result<List<Product>> = runCatching {
        val file = File(path)
        require(file.exists()) { "El archivo no existe en la ruta guardada." }

        val products = mutableListOf<Product>()
        file.inputStream().use { stream ->
            XSSFWorkbook(stream).use { workbook ->
                val sheet = workbook.getSheetAt(0)
                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue

                    val barcode = row.getCell(0)?.let { cell ->
                        when (cell.cellType) {
                            CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
                            CellType.STRING  -> cell.stringCellValue.trim()
                            else -> null
                        }
                    } ?: continue

                    val description = row.getCell(1)?.stringCellValue?.trim() ?: ""
                    val stock       = row.getCell(2)?.numericCellValue?.toInt() ?: 0
                    val cost        = row.getCell(3)?.numericCellValue ?: 0.0

                    products.add(Product(barcode, description, stock, cost))
                }
            }
        }
        products
    }
}
