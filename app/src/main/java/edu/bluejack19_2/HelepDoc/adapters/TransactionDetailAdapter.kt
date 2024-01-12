package edu.bluejack19_2.HelepDoc.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.CartViewHolder
import edu.bluejack19_2.HelepDoc.models.Cart

class TransactionDetailAdapter (val details: ArrayList<Cart>): RecyclerView.Adapter<CartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_transaction, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        var cart = details[position]
        holder.bindItemTransaction(cart)
    }
}