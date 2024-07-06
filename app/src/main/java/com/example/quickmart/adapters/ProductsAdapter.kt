package com.example.quickmart.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickmart.models.ProductModel
import com.example.quickmart.R
import com.example.quickmart.ui.DetailActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DecimalFormat

class ProductsAdapter(
    private val context: Context,
    private val list: ArrayList<ProductModel>,
    storageReference: FirebaseStorage
) :
    RecyclerView.Adapter<ProductsAdapter.MyHolder>() {

    private val storageReference: FirebaseStorage = storageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.tvPriceDiscounted.text =
            "$" + DecimalFormat("##.##").format(list?.get(position)?.price?.times(0.80))
        holder.tvPrice.text = "$" + list?.get(position)?.price.toString()
        holder.tvProductName.text = list?.get(position)?.name
        val imageRef: StorageReference =
            storageReference.getReferenceFromUrl("gs://quickmartapp2024.appspot.com/products/" + list!![position]?.imageUrl)
        Glide.with(context)
            .load(imageRef)
            .into(holder.imgProduct)
        holder.itemView.setOnClickListener {
            val i = Intent(context, DetailActivity::class.java)
            i.putExtra("item", list?.get(position))
            i.putExtra("productName", list?.get(position)?.name)
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPriceDiscounted: TextView = itemView.findViewById(R.id.tvPriceDiscounted)
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)

        init {
            tvPrice.paintFlags = tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }
}
