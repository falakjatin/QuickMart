package com.example.quickmart.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmart.R
import com.example.quickmart.adapters.CartAdapter
import com.example.quickmart.models.CartProductModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.DecimalFormat


class CartActivity : AppCompatActivity() {
    private val TAG = "CheckoutActivity"
    var img_product: ImageView? = null
    var imageBack: ImageView? = null
    var tv_product_name: TextView? = null
    var tv_amount: TextView? = null
    var tvSubTotal: TextView? = null
    var tvHst: TextView? = null
    var tvGrandTotal: TextView? = null
    var rvCart: RecyclerView? = null
    lateinit var btnCheckOut: Button
    lateinit var btnShop: Button
    var llPlaceHolder: LinearLayout? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var cartReference: DatabaseReference? = null
    var storage: FirebaseStorage? = null
    var currentUser: FirebaseUser? = null
    var mAuth: FirebaseAuth? = null
    private var cartList: ArrayList<CartProductModel>? = null
    private var progressDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser

        if (currentUser != null) {

            findViewById<ScrollView>(R.id.scrollView2).visibility = View.VISIBLE

            storage = FirebaseStorage.getInstance()
            firebaseDatabase = FirebaseDatabase.getInstance()
            cartReference = firebaseDatabase!!.getReference("carts")
            rvCart = findViewById(R.id.rvCart)
            tvGrandTotal = findViewById(R.id.tvGrandTotal)
            tvSubTotal = findViewById(R.id.tvSubTotal)
            tvHst = findViewById(R.id.tvHst)
            btnCheckOut = findViewById(R.id.btnCheckOut)
            btnCheckOut.setOnClickListener(View.OnClickListener {
                val i = Intent(
                    this@CartActivity,
                    CheckoutActivity::class.java
                )
                startActivity(i)
            })
            fetchCartData()
            btnShop = findViewById(R.id.btnShop)
            llPlaceHolder = findViewById(R.id.llPlaceHolder)
            btnShop.setOnClickListener(View.OnClickListener {
                val intent = Intent(
                    this@CartActivity,
                    ProductActivity::class.java
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            })
        } else {
            findViewById<LinearLayout>(R.id.llNoUser).visibility = View.VISIBLE

            findViewById<Button>(R.id.btnLogin).setOnClickListener(View.OnClickListener {
                val i = Intent(
                    this@CartActivity,
                    AuthSelectionActivity::class.java
                )
                startActivity(i)
                finish()
            })
        }

    }

    private fun fetchCartData() {
        showProgressDialog()
        currentUser?.let {
            cartReference?.child(it.uid)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        hideProgressDialog()
                        cartList = ArrayList()
                        for (item in snapshot.children) {
                            item.getValue(CartProductModel::class.java)?.let { cartList!!.add(it) }
                        }
                        if (cartList!!.size == 0) {
                            llPlaceHolder!!.visibility = View.VISIBLE
                        } else {
                            llPlaceHolder!!.visibility = View.GONE
                        }
                        val adapter =
                            storage?.let { CartAdapter(this@CartActivity, cartList!!, it) }
                        rvCart!!.adapter = adapter
                        val layoutManager =
                            LinearLayoutManager(
                                this@CartActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        rvCart!!.layoutManager = layoutManager
                        rvCart!!.setHasFixedSize(true)
                        adapter!!.setOnDeleteClickListener(object :
                            CartAdapter.OnDeleteClickListener {
                            override fun onDeleteClicked(productName: String?) {
                                deleteItem(productName!!)
                            }
                        })
                        var total = 0.0
                        for (p in cartList!!) {
                            total += p.quantity * p.price!!.toDouble()
                        }
                        tvSubTotal!!.text = String.format("Subtotal: $%.2f", total)
                        tvHst!!.text = String.format("HST(13%%): $%.2f", total * 0.13)
                        tvGrandTotal!!.text =
                            String.format("Grand Total: $%.2f", total + (total * 0.13))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        hideProgressDialog()
                        Log.e(TAG, "onCancelled: $error")
                    }
                })
        }
    }

    private fun deleteItem(productName: String) {
        showProgressDialog()
        currentUser?.let {
            cartReference?.child(it.getUid())?.child(productName)
                ?.removeValue { error, _ ->
                    hideProgressDialog()
                    if (error == null) {
                        fetchCartData()
                    }
                }
        }
    }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog!!.setContentView(R.layout.dialog_progress)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.cancel()
        }
    }

    private fun transferToLogin() {
        val i = Intent(this@CartActivity, CartActivity::class.java)
        startActivity(i)
        finish()
    }
}