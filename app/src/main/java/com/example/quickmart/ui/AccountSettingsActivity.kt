package com.example.quickmart.ui

import com.example.quickmart.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AccountSettingsActivity : AppCompatActivity() {
    var logout: Button? = null
    var login: Button? = null
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        currentUser = auth.currentUser

        logout = findViewById<Button>(R.id.logout)
        login = findViewById<Button>(R.id.login)

        if (currentUser == null) {
            login?.visibility = View.VISIBLE
            logout?.visibility = View.GONE
        } else {
            logout?.visibility = View.VISIBLE
            login?.visibility = View.GONE
        }

        logout?.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            login?.visibility = View.VISIBLE
            logout?.visibility = View.GONE
        })

        login?.setOnClickListener(View.OnClickListener {
            val intent = Intent(
                this@AccountSettingsActivity,
                LoginActivity::class.java
            )
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        })
    }
}