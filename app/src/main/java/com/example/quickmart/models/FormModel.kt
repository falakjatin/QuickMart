package com.example.quickmart.models

import com.google.android.material.textfield.TextInputLayout

class FormModel(
    type: String,
    field: TextInputLayout,
    value: String,
    error: String,
    compareStr: String
) {
    var type = ""
    var field: TextInputLayout
    var error: String
    var compareStr = ""
    var value = ""

    init {
        this.type = type
        this.field = field
        this.error = error
        this.compareStr = compareStr
        this.value = value
    }
}