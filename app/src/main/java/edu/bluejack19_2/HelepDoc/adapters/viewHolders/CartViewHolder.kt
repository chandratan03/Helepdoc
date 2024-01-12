package edu.bluejack19_2.HelepDoc.adapters.viewHolders

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.bluejack19_2.HelepDoc.ProductPage
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Cart

class CartViewHolder  constructor (var view: View): RecyclerView.ViewHolder(view){
    var ivProductImage: ImageView = view.findViewById(R.id.iv_product_image) as ImageView
    var tvProductName:TextView = view.findViewById(R.id.tv_product_name)
    var tvProductPrice: TextView = view.findViewById(R.id.tv_product_price)
    var tvQuantity:TextView = view.findViewById(R.id.tv_quantity)
    lateinit var tvSubTotal:TextView
    lateinit var btnAdd:Button
    lateinit var btnDelete:Button
    fun bindItem(cart:Cart){
        btnAdd = view.findViewById(R.id.btn_add)
        btnDelete = view.findViewById(R.id.btn_delete)
        tvProductName.text = cart.product.name
        tvProductPrice.text = "IDR " +cart.product.price.toInt().toString()
        Glide.with(itemView.context).load(cart.product.image).into(ivProductImage)
        tvQuantity.text = cart.quantity.toString()

    }

    fun bindItemTransaction(cart: Cart) {
        Log.v("BINDITEMTRANSACTION", cart.product.name)
        tvSubTotal = view.findViewById(R.id.tv_subtotal)
        tvProductName.text = "Product Name: " + cart.product.name
        tvProductPrice.text = "Product Price: IDR " + cart.product.price.toInt().toString()
        tvQuantity.text = "Quantity: " + cart.quantity.toString()
        tvSubTotal.text = "Sub Total: " + (cart.product.price * cart.quantity).toString()
        Glide.with(itemView.context).load(cart.product.image).into(ivProductImage)
    }

}