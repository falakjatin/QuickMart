package com.example.quickmart.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmart.R
import com.example.quickmart.models.OrderItem
import com.example.quickmart.adapters.OrderItemAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ordersRef: DatabaseReference
    private lateinit var orderItemAdapter: OrderItemAdapter
    private lateinit var storageReference: FirebaseStorage
    private val orderItemList = mutableListOf<OrderItem>()
    lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        ordersRef = database.getReference("orders")
        storageReference = FirebaseStorage.getInstance()
        toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewOrderHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)
        orderItemAdapter = OrderItemAdapter(orderItemList, storageReference)
        recyclerView.adapter = orderItemAdapter

        fetchOrderHistory()
    }

    private fun fetchOrderHistory() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            ordersRef.child(user.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderItemList.clear()
                    for (orderSnapshot in snapshot.children) {
                        val orderItemsSnapshot = orderSnapshot.child("orderItems")
                        for (itemSnapshot in orderItemsSnapshot.children) {
                            val orderItem = itemSnapshot.getValue(OrderItem::class.java)
                            orderItem?.let { orderItemList.add(it) }
                        }
                    }
                    orderItemAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@OrderHistoryActivity, "Failed to load order history.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
