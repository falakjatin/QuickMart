package com.example.quickmart.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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
                this@MainActivity,
                LoginActivity::class.java
            )
            startActivity(i)
            finish()
        })
        btnRegister.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@MainActivity,
                RegisterActivity::class.java
            )
            startActivity(i)
            finish()
        })
    }
}