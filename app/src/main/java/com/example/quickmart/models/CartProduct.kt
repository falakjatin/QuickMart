package com.example.quickmart.models

class CartProduct {
    var name: String? = null
    private var category: String? = null
    private var imageUrl: String? = null
    private var description: String? = null
    private var price: Double? = null
    private var quantity = 0

    constructor()
    constructor(
        name: String?,
        category: String?,
        imageUrl: String?,
        description: String?,
        price: Double?,
        quantity: Int
    ) {
        this.name = name
        this.category = category
        this.imageUrl = imageUrl
        this.description = description
        this.price = price
        this.quantity = quantity
    }
}

