package com.example.quickmart.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private var editTextEmail: TextInputEditText? = null
    private var editTextPassword: TextInputEditText? = null
    private var buttonLogin: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var textView: TextView? = null
    private var progressDialog: ProgressDialog? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var userReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseDatabase = FirebaseDatabase.getInstance()
        userReference = firebaseDatabase!!.getReference("users")
        mAuth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.btn_login)
        textView = findViewById(R.id.registerNow)

        textView?.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonLogin?.setOnClickListener {
            showProgressDialog()

            val email: String = editTextEmail?.text.toString()
            val password: String = editTextPassword?.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@LoginActivity, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                Toast.makeText(this@LoginActivity, "Enter valid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, "Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Password has at least 1 Character, 1 Digit & 1 Special Symbol",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    progressDialog?.cancel()
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(applicationContext, ProductActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Incorrect Password", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Please wait...")
        progressDialog?.show()
    }

    companion object {
        private val PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$")
        private val EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    }
}
