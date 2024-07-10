package com.example.quickmart.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.quickmart.models.CartProductModel
import com.example.quickmart.R
import com.example.quickmart.models.ProductModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailActivity : AppCompatActivity() {
    private var itemTitle: TextView? = null
    private var itemName: TextView? = null
    private var itemPrice: TextView? = null
    private var itemDesc: TextView? = null
    private var imageBack: ImageView? = null
    private var imgItem: ImageView? = null
    private var imgCart: ImageView? = null
    private var buyButton: Button? = null
    private var layoutProgress: LinearLayout? = null
    private var quantity = 1
    private var firebaseDatabase: FirebaseDatabase? = null
    private var cartReference: DatabaseReference? = null
    private var productsReference: DatabaseReference? = null
    private var currentUser: FirebaseUser? = null
    private var productModel: ProductModel? = null
    private var storageReference: FirebaseStorage? = null
    private var progressDialog: Dialog? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        firebaseDatabase = FirebaseDatabase.getInstance()
        cartReference = firebaseDatabase!!.getReference("carts")
        productsReference = firebaseDatabase!!.getReference("products")
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance()
        currentUser = auth.currentUser
        val productName = intent.getStringExtra("productName")

        itemTitle = findViewById(R.id.item_title)
        itemName = findViewById(R.id.item_name)
        itemPrice = findViewById(R.id.item_price)
        itemDesc = findViewById(R.id.item_desc)
        imageBack = findViewById(R.id.img_back)
        imgItem = findViewById(R.id.img_item)
        imgCart = findViewById(R.id.img_cart)
        buyButton = findViewById(R.id.buy_button)
        layoutProgress = findViewById(R.id.layoutProgress)

        getProductFromDB(productName)

        imageBack?.setOnClickListener { onBackPressed() }

        buyButton?.setOnClickListener { addToCart() }

        imgCart?.setOnClickListener {
            val i = Intent(this@DetailActivity, CartActivity::class.java)
            startActivity(i)
        }

        getQuantityFromCart(productName)
    }

    private fun getProductFromDB(productName: String?) {
        productsReference?.orderByChild("name")?.equalTo(productName)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
                        productModel = snap.getValue(ProductModel::class.java)
                    }
                    updateUI()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(this.javaClass.name, "onCancelled: " + error.message)
                }
            })
    }

    private fun updateUI() {
        itemTitle?.text = productModel?.name
        itemName?.text = productModel?.name
        itemPrice?.text = "$" + productModel?.price
        itemDesc?.text = productModel?.description
        val imageRef: StorageReference =
            storageReference!!.getReferenceFromUrl("gs://quickmartapp2024.appspot.com/products/" + productModel?.imageUrl)
        Glide.with(this).load(imageRef).into(imgItem!!)
    }

    private fun getQuantityFromCart(productName: String?) {
        if (productName != null) {
            if (currentUser != null) {
                cartReference?.child(currentUser?.uid!!)?.child(productName)
                    ?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val cartItem: CartProductModel? =
                                snapshot.getValue(CartProductModel::class.java)
                            if (cartItem == null) {
                                setUpQuantityButton(1)
                            } else {
                                setUpQuantityButton(cartItem.quantity)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(this.javaClass.name, "onCancelled: " + error.message)
                        }
                    })
            } else {
                setUpQuantityButton(1)
            }
        }
    }

    private fun addToCart() {
        if (currentUser != null) {
            showProgressDialog()
            cartReference
                ?.child(currentUser?.uid!!)
                ?.child(productModel?.name!!)
                ?.setValue(
                    CartProductModel(
                        productModel?.name,
                        productModel?.category,
                        productModel?.imageUrl,
                        productModel?.description,
                        productModel?.price,
                        quantity
                    )
                )?.addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(this@DetailActivity, "Added to cart.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            this.transferToLogin()
        }

    }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.dialog_progress)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.cancel()
    }

    private fun setUpQuantityButton(q: Int) {
        val ibAdd = findViewById<ImageButton>(R.id.ibAdd)
        val ibMinus = findViewById<ImageButton>(R.id.ib_minus)
        val tvQuantity = findViewById<TextView>(R.id.tvQuantity)
        quantity = q
        tvQuantity.text = quantity.toString()
        ibMinus.setOnClickListener {
            if (quantity != 0) {
                quantity = quantity - 1
                tvQuantity.text = quantity.toString()
            }
        }
        ibAdd.setOnClickListener {
            if (quantity < 10) {
                quantity = quantity + 1
                tvQuantity.text = quantity.toString()
            } else {
                Toast.makeText(
                    this@DetailActivity,
                    "Maximum order capacity is 10.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun transferToLogin() {
        val i = Intent(this@DetailActivity, AuthSelectionActivity::class.java)
        startActivity(i)
        finish()
    }
}
