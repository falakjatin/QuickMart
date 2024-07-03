package com.example.quickmart.models


class Category {
    var name: String? = null
    var img: String? = null

    constructor()
    constructor(name: String?, img: String?) {
        this.name = name
        this.img = img
    }
}

