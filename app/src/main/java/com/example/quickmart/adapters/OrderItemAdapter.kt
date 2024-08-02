package com.example.quickmart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickmart.R
import com.example.quickmart.models.OrderItem
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class OrderItemAdapter(
    private val orderItemList: List<OrderItem>,
    private val storageReference: FirebaseStorage
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = orderItemList[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int = orderItemList.size

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)

        fun bind(orderItem: OrderItem) {
            productName.text = orderItem.productName
            productPrice.text = "Price: $${orderItem.price}"
            productQuantity.text = "Quantity: ${orderItem.quantity}"

            val imageRef: StorageReference =
                storageReference.getReferenceFromUrl("gs://quickmartapp2024.appspot.com/products/" + orderItem.imageUrl)

            Glide.with(itemView.context)
                .load(imageRef)
                .into(productImage)
        }
    }
}
