package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// 1. Classe Product pour représenter les données
data class Product(
    val name: String,
    val brand: String,
    val store: String,
    val price: String,
    val image: String?
)

// 2. Adapter
class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // 3. ViewHolder : chaque item du RecyclerView
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvBrand: TextView = itemView.findViewById(R.id.tvBrand)
        val tvStore: TextView = itemView.findViewById(R.id.tvStore)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text = product.name
        holder.tvBrand.text = product.brand
        holder.tvStore.text = product.store
        holder.tvPrice.text = product.price

        if (!product.image.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.image)
                .into(holder.ivProduct)
        }

        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount(): Int = products.size
}
