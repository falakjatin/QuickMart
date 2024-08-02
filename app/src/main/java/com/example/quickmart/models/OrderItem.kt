package com.example.quickmart.models

data class OrderItem(
    val imageUrl: String = "",
    val price: Double = 0.0,
    val productName: String = "",
    val quantity: Int = 0
)
