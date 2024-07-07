package com.example.quickmart.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickmart.models.CartProduct
import com.example.quickmart.ui.DetailActivity
import com.example.quickmart.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DecimalFormat


class CartAdapter(
    private val context: Context,
    private val list: ArrayList<CartProduct>,
    storageReference: FirebaseStorage
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private val storageReference: FirebaseStorage
    private var onDeleteClickListener: OnDeleteClickListener? = null

    init {
        this.storageReference = storageReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_cart_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        val imageRef: StorageReference =
            storageReference.getReferenceFromUrl("gs://indian-superstore.appspot.com/products/" + product.imageUrl)
        Glide.with(context)
            .load(imageRef)
            .into(holder.img)
        holder.tvName.setText(product.name)
        holder.tvRate.text = "Rate: $" + product.price
        holder.tvQty.text = "Qty: " + product.quantity
        holder.tvPrice.text =
            "Price: $" + DecimalFormat("##.##").format(product.quantity * product.price!!)
        holder.ibDelete.setOnClickListener {
            if (onDeleteClickListener != null) {
                onDeleteClickListener!!.onDeleteClicked(product.name)
            }
        }
        holder.itemView.setOnClickListener {
            val i = Intent(context, DetailActivity::class.java)
            i.putExtra("productName", list[position].name)
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView
        var ibDelete: ImageButton
        var tvName: TextView
        var tvRate: TextView
        var tvQty: TextView
        var tvPrice: TextView

        init {
            img = itemView.findViewById(R.id.img)
            tvName = itemView.findViewById(R.id.tvName)
            tvRate = itemView.findViewById(R.id.tvRate)
            tvQty = itemView.findViewById(R.id.tvQty)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            ibDelete = itemView.findViewById(R.id.ibDelete)
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClicked(productName: String?)
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener?) {
        this.onDeleteClickListener = onDeleteClickListener
    }
}