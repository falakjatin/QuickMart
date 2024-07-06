package com.example.quickmart.utils

import android.util.Log
import com.example.quickmart.models.FormModel
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

class ValidationUtil(form: Array<FormModel?>) {
    var isAllValid = true
        private set

    init {
        for (formModel in form) {
            if (formModel != null) {
                returnError(
                    formModel.field,
                    formModel.value,
                    formModel.type,
                    formModel.compareStr,
                    formModel.error
                )
            }
        }
    }

    private fun returnError(
        field: TextInputLayout,
        value: String,
        type: String,
        toCompare: String,
        error: String
    ) {
        val modError = error != ""
        when (type) {
            "email" -> {
                if (value == "") {
                    isAllValid = false
                    field.error = if (modError) error else "This field is required!"
                } else {
                    val isValid = Pattern.compile(
                        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                        Pattern.CASE_INSENSITIVE
                    ).matcher(value).matches()
                    if (isValid) {
                        field.error = null
                    } else {
                        isAllValid = false
                        field.error = if (modError) error else "Please enter a valid Email Address!"
                    }
                }
                return
            }

            "mobile" -> {
                if (value == "") {
                    isAllValid = false
                    field.error = if (modError) error else "This field is required!"
                } else {
                    val isValid = Pattern.compile(
                        "^(\\+\\d{1}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
                        Pattern.CASE_INSENSITIVE
                    ).matcher(value).matches()
                    if (isValid) {
                        field.error = null
                    } else {
                        isAllValid = false
                        field.error = if (modError) error else "Please enter a valid Email Address!"
                    }
                }
                return
            }

            "no_numeric_special" -> {
                if (value == "") {
                    isAllValid = false
                    field.error = if (modError) error else "This field is required!"
                } else {
                    val isValid =
                        Pattern.compile("^[a-zA-Z]*$", Pattern.CASE_INSENSITIVE).matcher(value)
                            .matches()
                    if (isValid) {
                        field.error = null
                    } else {
                        isAllValid = false
                        field.error = if (modError) error else "Please enter a valid Email Address!"
                    }
                }
                return
            }

            "postal" -> {
                if (value == "") {
                    isAllValid = false
                    field.error = if (modError) error else "This field is required!"
                } else {
                    val isValid = Pattern.compile(
                        "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$",
                        Pattern.CASE_INSENSITIVE
                    ).matcher(value).matches()
                    if (isValid) {
                        field.error = null
                    } else {
                        isAllValid = false
                        field.error = if (modError) error else "Please enter a valid Postal Code!"
                    }
                }
                return
            }

            "password" -> {
                if (value == "") {
                    field.error = if (modError) error else "This field is required!"
                } else {
                    val isValid = Pattern.compile(
                        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                        Pattern.CASE_INSENSITIVE
                    ).matcher(value).matches()
                    if (isValid) {
                        field.error = null
                    } else {
                        isAllValid = false
                        field.error = if (modError) error else "Please enter a valid password!" +
                                "\n\tContains 1 Upper case letter" +
                                "\n\tContains 1 Lower case letter" +
                                "\n\tContains 1 number" +
                                "\n\tContains 1 special character" +
                                "\n\tNo space" +
                                "\n\tMinimum 8 character are required"
                    }
                }
                return
            }

            "cvv" -> {
                if (value == "") {
                    isAllValid = false
                    field.error = if (modError) error else "This field is required!"
                } else {
                    val isValid =
                        Pattern.compile("/^[0-9]{3,4}$/", Pattern.CASE_INSENSITIVE).matcher(value)
                            .matches()
                    if (isValid) {
                        field.error = null
                    } else {
                        isAllValid = false
                        field.error = if (modError) error else "Please enter a valid CVV Code!"
                    }
                }
                return
            }

            "compare" -> {
                if (value == toCompare) {
                    field.error = null
                } else {
                    isAllValid = false
                    field.error =
                        if (modError) error else "This field does not match the above value"
                }
                return
            }

            else -> {
                Log.i("VALIDATION TEST", "returnError: " + (value == ""))
                if (value == "") {
                    isAllValid = false
                    field.error = if (modError) error else "This field is required!"
                    return
                }
                field.error = null
            }
        }
    }
}