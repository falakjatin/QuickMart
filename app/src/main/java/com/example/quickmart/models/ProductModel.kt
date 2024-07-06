package com.example.quickmart.models;

import java.io.Serializable


class ProductModel : Serializable {
    var name: String? = null
    var category: String? = null
    var imageUrl: String? = null
    var description: String? = null
    var price: Double? = null

    constructor()
    constructor(
            name: String?,
            category: String?,
            imageUrl: String?,
            description: String?,
            price: Double
    ) {
        this.name = name
        this.category = category
        this.imageUrl = imageUrl
        this.description = description
        this.price = price
    }
}
