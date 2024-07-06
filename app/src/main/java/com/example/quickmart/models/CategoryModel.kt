package com.example.quickmart.models


class CategoryModel {
    var name: String? = null
    var img: String? = null

    constructor()
    constructor(name: String?, img: String?) {
        this.name = name
        this.img = img
    }
}

