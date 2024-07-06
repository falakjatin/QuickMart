package com.example.quickmart.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.quickmart.R
import com.example.quickmart.adapters.ProductsAdapter
import com.example.quickmart.models.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class CategoryActivity : AppCompatActivity() {
    lateinit var rvProducts: RecyclerView
    lateinit var toolbar: Toolbar
    private var db: FirebaseDatabase? = null
    private var productReference: DatabaseReference? = null
    private lateinit var list: ArrayList<ProductModel>
    private var adapter: ProductsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        val name = intent.getStringExtra("name")
        rvProducts = findViewById(R.id.rvProducts)
        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(name)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })
        db = FirebaseDatabase.getInstance()
        productReference = db!!.getReference("products")
        getDataFromDB(name)
    }

    private fun getDataFromDB(name: String?) {
        productReference?.orderByChild("category")?.equalTo(name)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list = ArrayList()
                    for (productSnap in snapshot.children) {
                        list.add(productSnap.getValue(ProductModel::class.java)!!)
                    }
                    adapter = ProductsAdapter(this@CategoryActivity, list, FirebaseStorage.getInstance())
                    rvProducts!!.setAdapter(adapter)
                    rvProducts!!.layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", error.message)
                }
            })
    }
}