package com.example.quickmart.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class AuthSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_auth_selection)

        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        if (auth.currentUser != null) {
            val productIntent = Intent(this, ProductActivity::class.java)
            startActivity(productIntent)
            finish()
        }

        btnLogin.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@AuthSelectionActivity,
                LoginActivity::class.java
            )
            startActivity(i)
            finish()
        })
        btnRegister.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@AuthSelectionActivity,
                RegisterActivity::class.java
            )
            startActivity(i)
            finish()
        })
    }
}