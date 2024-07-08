package com.example.quickmart.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.R
import com.example.quickmart.models.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Objects
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    lateinit var editTextEmail: TextInputEditText
    lateinit var editTextPassword: TextInputEditText
    lateinit var etMobileNo: TextInputEditText
    lateinit var etFirstName: TextInputEditText
    lateinit var etLastName: TextInputEditText
    lateinit var buttonReg: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    private lateinit  var progressDialog: ProgressDialog
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var userReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        userReference = firebaseDatabase!!.getReference("users")
        editTextEmail = findViewById(R.id.register_email)
        editTextPassword = findViewById(R.id.password)
        buttonReg = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.loginNow)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etMobileNo = findViewById(R.id.etMobileNo)
        textView.setOnClickListener(View.OnClickListener {
            val intent = Intent(
                applicationContext,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        })
        buttonReg.setOnClickListener(View.OnClickListener {
            progressBar.setVisibility(View.VISIBLE)
            val email: String
            val password: String
            val firstName: String
            val lastName: String
            val mobileNo: String
            email = editTextEmail.getText().toString()
            password = editTextPassword.getText().toString()
            firstName = etFirstName.getText().toString()
            lastName = etLastName.getText().toString()
            mobileNo = etMobileNo.getText().toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@RegisterActivity, "Enter Email", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                Toast.makeText(this@RegisterActivity, "Enter valid Email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@RegisterActivity, "Enter Password", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password has atleast 1 Chracter, 1 Digit & 1 Special Symbol & 8 Chacters Long",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            showProgressDialog()
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult?>{
                    override fun onComplete(task: Task<AuthResult?>) {
                        progressBar.setVisibility(View.GONE)
                        hideProgressDialog()
                        if (task.isSuccessful()) {
                            addUser(email, password, firstName, lastName, mobileNo)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                this@RegisterActivity, "Invalid credentials",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        })
    }

    private fun addUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        mobileNo: String
    ) {
        showProgressDialog()
        userReference.child(Objects.requireNonNull(mAuth.getCurrentUser())!!.uid)
            .setValue(UserModel(email, password, firstName, lastName, mobileNo))
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {
                    hideProgressDialog()
                    if (task.isSuccessful()) {
                        Toast.makeText(
                            this@RegisterActivity, "Account Created",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(
                            applicationContext,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    }
                }
            })
    }

    fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.cancel()
        }
    }

    companion object {
        private val PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$")
        private val EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    }
}