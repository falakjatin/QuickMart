package com.example.quickmart.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.quickmart.R
import com.example.quickmart.models.UserModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    lateinit var toolbar: MaterialToolbar
    lateinit var tvFirstname: TextView
    lateinit var tvLastName: TextView
    lateinit var tvMobileNo: TextView
    lateinit var tvEmail: TextView
    lateinit var btnLogout: Button
    lateinit var btnLogin: Button
    lateinit var btnOrderHistory: Button
    lateinit var switchTheme: Switch
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var userRef: DatabaseReference
    var currentUser: FirebaseUser? = null
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("users")
        currentUser = auth.currentUser

        toolbar = findViewById(R.id.toolbar)
        switchTheme = findViewById(R.id.switchTheme)
        tvFirstname = findViewById(R.id.tvFirstName)
        tvLastName = findViewById(R.id.tvLastName)
        tvMobileNo = findViewById(R.id.tvMobileNo)
        tvEmail = findViewById(R.id.tvEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnLogin = findViewById(R.id.btnLogin)
        btnOrderHistory = findViewById(R.id.btnOrderHistory)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        changeView(currentUser)

        btnLogout.setOnClickListener {
            auth.signOut()
            changeView(null)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this@ProfileActivity, AuthSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        btnOrderHistory.setOnClickListener {
            val intent = Intent(this@ProfileActivity, OrderHistory::class.java)
            startActivity(intent)
        }
    }

    private fun changeView(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            findViewById<LinearLayout>(R.id.llUserInfo).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.llNoUser).visibility = View.GONE
            userData
        } else {
            findViewById<LinearLayout>(R.id.llUserInfo).visibility = View.GONE
            findViewById<LinearLayout>(R.id.llNoUser).visibility = View.VISIBLE
        }
    }

    private val userData: Unit
        get() {
            showProgressDialog()
            currentUser?.let {
                userRef.child(it.uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            hideProgressDialog()
                            val user: UserModel? = snapshot.getValue(UserModel::class.java)
                            if (user != null) {
                                tvFirstname.text = user.firstname
                                tvLastName.text = user.lastname
                                tvMobileNo.text = user.mobileno
                                tvEmail.text = user.email
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            hideProgressDialog()
                            Toast.makeText(this@ProfileActivity, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog.apply {
            setContentView(R.layout.dialog_progress)
            setCancelable(false)
            show()
        }
    }

    private fun hideProgressDialog() {
        progressDialog.cancel()
    }
}
