package com.stockscanner.app.data.model

data class Product(
    val barcode: String,
    val description: String,
    val stock: Int,
    val costWithoutVat: Double
)
