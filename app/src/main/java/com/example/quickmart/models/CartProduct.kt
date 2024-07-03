package com.example.quickmart.models

import java.io.Serializable


class CartProduct : Serializable {
    var name: String? = null
    var category: String? = null
    var imageUrl: String? = null
    var description: String? = null
    var price: Double? = null
    var quantity = 0

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

