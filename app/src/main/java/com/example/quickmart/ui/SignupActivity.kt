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
import android.util.Log
import android.view.View
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

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val mAuth = FirebaseAuth.getInstance()
        val loginHere = findViewById<TextView>(R.id.login_here)
        val loginHereText = getString(R.string.already_have_an_account_login_here)
        val email = findViewById<TextInputEditText>(R.id.email_field)
        val emailLayout = findViewById<TextInputLayout>(R.id.email_input_layout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.password_layout)
        val password = findViewById<TextInputEditText>(R.id.password_field)
        val confirmPassword = findViewById<TextInputEditText>(R.id.confirm_password_field)
        val confirmPasswordLayout = findViewById<TextInputLayout>(R.id.confirm_password_layout)
        val signupBtn = findViewById<View>(R.id.signup)
        val ss = SpannableString(loginHereText)
        val fcsSignUp = ForegroundColorSpan(Color.BLUE)
        ss.setSpan(fcsSignUp, 25, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginHere.text = ss
        signupBtn.setOnClickListener { e: View? ->
            val emailStr =
                Objects.requireNonNull(email.getText()).toString()
            val passwordStr =
                Objects.requireNonNull(password.getText()).toString()
            val form: Array<FormModel?> = arrayOfNulls<FormModel>(3)
            form[0] = FormModel("email", emailLayout, emailStr, "", "")
            form[1] = FormModel("password", passwordLayout, passwordStr, "", "")
            form[2] = FormModel(
                "compare",
                confirmPasswordLayout,
                Objects.requireNonNull<Editable?>(confirmPassword.getText()).toString(),
                "",
                password.getText().toString()
            )
            val validate = ValidationUtil(form)
            if (validate.isAllValid) {
                mAuth!!.createUserWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(
                        this
                    ) { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success")
                            redirectToLogin()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(
                                "TAG",
                                "createUserWithEmail:failure",
                                task.exception
                            )
                            Toast.makeText(
                                this@SignupActivity,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        loginHere.setOnClickListener { e: View? -> redirectToLogin() }
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
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                confirmPasswordLayout.error = null
            }
        })
    }

    private fun redirectToLogin() {
        val intent = Intent(
            this@SignupActivity,
            LoginActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}