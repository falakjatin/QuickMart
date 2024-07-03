package com.example.quickmart.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickmart.models.Category
import com.example.quickmart.ui.CategoryActivity
import com.example.quickmart.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CategoryAdapter(
    var list: ArrayList<Category>,
    var context: Context,
    storageReference: FirebaseStorage
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private val storageReference: FirebaseStorage
    var is_viewAll = false

    init {
        this.storageReference = storageReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageRef: StorageReference =
            storageReference.getReferenceFromUrl("gs://quickmartapp2024.appspot.com/category/" + list[position]?.img)
        Glide.with(context).load(imageRef).into(holder.imgCat)
        holder.tvName.text = list[position].name
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    CategoryActivity::class.java
                ).putExtra("name", list[position]?.name)
            )
        }
    }

    override fun getItemCount(): Int {
        return if (is_viewAll) list.size else {
            if (list.size > 6) {
                6
            } else {
                list.size
            }
        }
    }

    fun setViewAll(isViewAll: Boolean) {
        this.is_viewAll = isViewAll
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgCat: ImageView
        var tvName: TextView

        init {
            imgCat = itemView.findViewById(R.id.imgCat)
            tvName = itemView.findViewById(R.id.tvName)
        }
    }
}
