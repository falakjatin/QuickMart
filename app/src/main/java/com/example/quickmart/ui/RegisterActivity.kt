package com.example.quickmart.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.R
import com.example.quickmart.models.FormModel
import com.example.quickmart.models.UserModel
import com.example.quickmart.utils.ValidationUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    lateinit var editTextConfirmPassword: TextInputEditText
    lateinit var etMobileNo: TextInputEditText
    lateinit var etFirstName: TextInputEditText
    lateinit var etLastName: TextInputEditText

    lateinit var ltEmail: TextInputLayout
    lateinit var ltPassword: TextInputLayout
    lateinit var ltMobileNo: TextInputLayout
    lateinit var ltFirstName: TextInputLayout
    lateinit var ltConfirmPassword: TextInputLayout
    lateinit var ltLastName: TextInputLayout

    lateinit var buttonReg: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    private lateinit var progressDialog: ProgressDialog
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var userReference: DatabaseReference
    private lateinit var form: Array<FormModel?>
    lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        userReference = firebaseDatabase.getReference("users")
        editTextEmail = findViewById(R.id.register_email)
        editTextPassword = findViewById(R.id.password)
        editTextConfirmPassword = findViewById(R.id.etPasswordConfirm)
        buttonReg = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.loginNow)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etMobileNo = findViewById(R.id.etMobileNo)
        ltEmail = findViewById(R.id.etEmail_layout)
        ltPassword = findViewById(R.id.etPassword_layout)
        ltFirstName = findViewById(R.id.etFirstName_layout)
        ltLastName = findViewById(R.id.etLastName_layout)
        ltMobileNo = findViewById(R.id.etMobileNo_layout)
        ltConfirmPassword = findViewById(R.id.etPasswordConfirm_layout)
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        textView.setOnClickListener(View.OnClickListener {
            val intent = Intent(
                applicationContext,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        })
        buttonReg.setOnClickListener(View.OnClickListener {
            val email = editTextEmail.getText().toString()
            val password = editTextPassword.getText().toString()
            val confirmPassword = editTextConfirmPassword.getText().toString()
            val firstName = etFirstName.getText().toString()
            val lastName = etLastName.getText().toString()
            val mobileNo = etMobileNo.getText().toString()

            form = arrayOfNulls(6)
            form[0] = FormModel(
                "default",
                ltFirstName, firstName, "", ""
            )
            form[1] = FormModel(
                "default",
                ltLastName, lastName, "", ""
            )
            form[2] = FormModel(
                "mobile",
                ltMobileNo,
                mobileNo,
                "",
                ""
            )
            form[3] = FormModel(
                "email",
                ltEmail,
                email,
                "",
                ""
            )
            form[4] = FormModel(
                "password",
                ltPassword,
                password,
                "",
                ""
            )
            form[5] = FormModel(
                "compare",
                ltConfirmPassword,
                confirmPassword,
                "",
                password
            )

            val validate = ValidationUtil(form)
            if (validate.isAllValid) {
                showProgressDialog()
                progressBar.visibility = View.VISIBLE
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = View.GONE
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            addUser(email, password, firstName, lastName, mobileNo)
                        } else {
                            Toast.makeText(
                                this@RegisterActivity, "Invalid credentials",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
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