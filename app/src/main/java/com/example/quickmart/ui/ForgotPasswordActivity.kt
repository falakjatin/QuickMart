package com.example.quickmart.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.quickmart.R
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.models.FormModel
import com.example.quickmart.utils.ValidationUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val email = findViewById<TextInputEditText>(R.id.email_field)
        val emailLayout = findViewById<TextInputLayout>(R.id.email_input_layout)
        val sendLink = findViewById<View>(R.id.send_link)
        sendLink.setOnClickListener { e: View? ->
            val form: Array<FormModel?> = arrayOfNulls<FormModel>(1)
            form[0] = FormModel("email", emailLayout, email.getText().toString(), "", "")
            val validate = ValidationUtil(form)
            if (validate.isAllValid) {
                val auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Reset Link has been sent to your email address.",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(
                                this@ForgotPasswordActivity,
                                LoginActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        }
                    }
            }
        }
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                emailLayout.error = null
            }
        })
    }
}