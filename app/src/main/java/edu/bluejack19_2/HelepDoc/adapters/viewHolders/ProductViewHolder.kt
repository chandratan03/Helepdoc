package edu.bluejack19_2.HelepDoc.adapters.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.ProductAdapter
import edu.bluejack19_2.HelepDoc.models.Product

class ProductViewHolder  constructor (view: View): RecyclerView.ViewHolder(view){
    var ivProductImage:ImageView = view.findViewById(R.id.iv_product_image) as ImageView
    var tvProductName:TextView = view.findViewById(R.id.tv_product_name)
    var tvProductPrice:TextView = view.findViewById(R.id.tv_product_price)
    var tvProductRate:TextView = view.findViewById(R.id.tv_product_rating)
    fun bindItem(product: Product){
        ivProductImage.setImageResource(R.drawable.sample_product)
        tvProductName.text = product.name
        tvProductPrice.text= "IDR " +product.price.toInt().toString()
        Glide.with(itemView.context).load(product.image).into(ivProductImage)
        tvProductRate.text = "Rate: "+ product.rating.toString()

    }

}