package com.example.quickmart.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickmart.R
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast

class CheckoutActivity : AppCompatActivity() {
    private lateinit var rgPaymentMode: RadioGroup
    private lateinit var layoutCard: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check_out)

        

        rgPaymentMode = findViewById(R.id.rgPaymentMode)
        layoutCard = findViewById(R.id.layoutCard)

        rgPaymentMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPOD -> {
                    layoutCard.visibility = View.GONE
                    Toast.makeText(this, "Selected: Pay on Delivery", Toast.LENGTH_SHORT).show()
                    Log.d("CheckoutActivity", "Pay on Delivery selected")
                }
                R.id.rbDebit -> {
                    layoutCard.visibility = View.VISIBLE
                    Toast.makeText(this, "Selected: Credit or Debit", Toast.LENGTH_SHORT).show()
                    Log.d("CheckoutActivity", "Credit or Debit selected")
                }
            }
        }
        if (rgPaymentMode.checkedRadioButtonId == R.id.rbDebit) {
            layoutCard.visibility = View.VISIBLE
        } else {
            layoutCard.visibility = View.GONE
        }
        
        
    }
}
