package com.example.quickmart.ui

import com.example.quickmart.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.models.FormModel
import com.example.quickmart.utils.ValidationUtil
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.Objects

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mAuth = FirebaseAuth.getInstance()
        val email = findViewById<TextInputEditText>(R.id.email_field)
        val password = findViewById<TextInputEditText>(R.id.password_field)
        val emailLayout = findViewById<TextInputLayout>(R.id.email_input_layout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.password_layout)
        val signUpHere = findViewById<TextView>(R.id.sign_up_here)
        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        val loginBtn = findViewById<Button>(R.id.login)
        val signUpHereText = getString(R.string.don_t_have_an_account_sign_up_here)
        val ss = SpannableString(signUpHereText)
        val fcsSignUp = ForegroundColorSpan(Color.BLUE)
        ss.setSpan(fcsSignUp, 23, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signUpHere?.text = ss
        loginBtn?.setOnClickListener(View.OnClickListener {
            val emailStr =
                Objects.requireNonNull(email?.getText()).toString()
            val passwordStr =
                Objects.requireNonNull(password?.getText()).toString()
            val form: Array<FormModel?> = arrayOfNulls<FormModel>(2)
            form[0] = FormModel("email", emailLayout, emailStr, "", "")
            form[1] = FormModel("password", passwordLayout, passwordStr, "", "")
            val validate = ValidationUtil(form)
            if (validate.isAllValid) {
                mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(
                        this
                    ) { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@LoginActivity, "Signed in successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@LoginActivity,
                                ProductActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        })
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                emailLayout.error = null
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                passwordLayout.error = null
            }
        })
        signUpHere.setOnClickListener(View.OnClickListener { e: View? ->
            val intent = Intent(
                this@LoginActivity,
                SignupActivity::class.java
            )
            startActivity(intent)
        })
        forgotPassword.setOnClickListener(View.OnClickListener { e: View? ->
            val intent = Intent(
                this@LoginActivity,
                ForgotPasswordActivity::class.java
            )
            startActivity(intent)
        })
    }
}