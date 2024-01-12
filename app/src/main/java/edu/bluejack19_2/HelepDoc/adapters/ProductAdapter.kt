package edu.bluejack19_2.HelepDoc.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.bluejack19_2.HelepDoc.ProductDetail
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.ProductViewHolder
import edu.bluejack19_2.HelepDoc.models.Product

class ProductAdapter(val products: ArrayList<Product>, val context:Context): RecyclerView.Adapter<ProductViewHolder>()    {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product, parent, false)
        return ProductViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product:Product = products[position]
        holder.bindItem(product)
//        holder.itemView.isClickable = true

        holder.itemView.setOnClickListener(View.OnClickListener {

            var intent:Intent = Intent(context, ProductDetail::class.java)
            intent.putExtra("productId", product.id)
//            Log.i("ID", product.id)
            context.startActivity(intent)
        })



    }


}