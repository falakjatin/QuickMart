package com.example.quickmart.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.quickmart.R
import com.example.quickmart.adapters.CategoryAdapter
import com.example.quickmart.adapters.ProductsAdapter
import com.example.quickmart.models.Category
import com.example.quickmart.models.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Collections

class ProductActivity : AppCompatActivity() {

    private var db: FirebaseDatabase? = null
    private var productReference: DatabaseReference? = null
    private var categoryReference: DatabaseReference? = null
    private var rvProducts: RecyclerView? = null
    private var rvCategory: RecyclerView? = null
    private var toolbar: MaterialToolbar? = null
    private var layoutProgress: LinearLayout? = null
    private var scrollView: NestedScrollView? = null
    private var cgCategory: ChipGroup? = null
    private var list: ArrayList<Product?>? = null
    private var adapter: ProductsAdapter? = null
    private var tvViewAll: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_product)

        db = FirebaseDatabase.getInstance()
        productReference = db!!.getReference("products")
        categoryReference = db!!.getReference("category")
        rvProducts = findViewById(R.id.rv_products)
        rvCategory = findViewById(R.id.rvCategory)
        tvViewAll = findViewById(R.id.tvViewAll)
        layoutProgress = findViewById(R.id.layoutProgress)
        scrollView = findViewById(R.id.scrollView)
        toolbar = findViewById(R.id.toolbar)


        toolbar?.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_cart) {
                val i = Intent(this@ProductActivity, CartActivity::class.java)
                startActivity(i)
            }
            true
        }

//        toolbar?.setNavigationOnClickListener {
//            val i = Intent(this@ProductActivity, ProfileActivity::class.java)
//            startActivity(i)
//        }

        dataFromDB()

    }

    private fun dataFromDB() {
        layoutProgress?.visibility = View.VISIBLE
        scrollView?.visibility = View.INVISIBLE

        productReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                layoutProgress?.visibility = View.INVISIBLE
                scrollView?.visibility = View.VISIBLE
                list = ArrayList()
                for (productSnap in snapshot.children) {
                    list?.add(productSnap.getValue(Product::class.java))
                }

                val randomList: ArrayList<Product> = ArrayList()
                if (list?.size ?: 0 > 15) {
                    Collections.shuffle(list)
                    randomList.addAll((list?.subList(0, 15) ?: emptyList()) as Collection<Product>)
                }

                setUpCategory()

                adapter = ProductsAdapter(
                    this@ProductActivity,
                    randomList,
                    FirebaseStorage.getInstance()
                )
                rvProducts?.adapter = adapter
                rvProducts?.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }

            override fun onCancelled(error: DatabaseError) {
                layoutProgress?.visibility = View.INVISIBLE
                scrollView?.visibility = View.VISIBLE
                Log.e("TAG", error.message)
            }
        })
    }

    private fun setUpCategory() {
        layoutProgress?.visibility = View.VISIBLE
        scrollView?.visibility = View.INVISIBLE

        val categoryList: ArrayList<Category> = ArrayList()

        categoryReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                layoutProgress?.visibility = View.INVISIBLE
                scrollView?.visibility = View.VISIBLE

                for (productSnap in snapshot.children) {
                    categoryList.add(productSnap.getValue(Category::class.java)!!)
                }

                val adapter = CategoryAdapter(
                    categoryList,
                    this@ProductActivity,
                    FirebaseStorage.getInstance()
                )
                rvCategory?.adapter = adapter
                rvCategory?.layoutManager = GridLayoutManager(this@ProductActivity, 3)

                tvViewAll?.setOnClickListener {
                    if (tvViewAll?.text == "View more") {
                        adapter.setViewAll(true)
                        tvViewAll?.text = "View less"
                    } else {
                        adapter.setViewAll(false)
                        tvViewAll?.text = "View more"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                layoutProgress?.visibility = View.INVISIBLE
                scrollView?.visibility = View.VISIBLE
                Log.e("TAG", error.message)
            }
        })
    }

    }
