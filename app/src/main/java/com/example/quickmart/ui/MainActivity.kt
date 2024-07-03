package com.example.quickmart.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var btnLogin: Button? = null
    private var btnRegister: Button? = null
    private var textView: TextView? = null
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
//        auth = FirebaseAuth.getInstance()

        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        textView = findViewById(R.id.textView3)
//        user = auth?.currentUser

//        if (user != null) {
            val productIntent = Intent(this, ProductActivity::class.java)
            startActivity(productIntent)
//            finish()
//        }



    }
}