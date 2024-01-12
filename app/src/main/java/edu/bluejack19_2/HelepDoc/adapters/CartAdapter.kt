package edu.bluejack19_2.HelepDoc.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack19_2.HelepDoc.CartActivity
import edu.bluejack19_2.HelepDoc.CartActivity.Companion.totalPayment
import edu.bluejack19_2.HelepDoc.ProductPage
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.CartViewHolder
import edu.bluejack19_2.HelepDoc.models.Cart


class CartAdapter (val carts:ArrayList<Cart>,var cartPage: CartActivity): RecyclerView.Adapter<CartViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.cart, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return carts.size
    }



    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        var cart:Cart = carts[position]
        holder.bindItem(cart)


        holder.btnAdd.setOnClickListener(View.OnClickListener {
            cart!!.quantity += 1
            holder.tvQuantity.text = cart!!.quantity.toString()
            cartPage.updateTotalPayment()
            cartPage.checkButton()
        })
        holder.btnDelete.setOnClickListener(View.OnClickListener {
            if(cart!!.quantity <= 1){
                carts.remove(cart!!)
//                notifyItemRemoved(position)
//                Log.i("hehe", carts.size.toString())

//                notifyDataSetChanged()
                cartPage.updateTotalPayment()
            }else{
                cart!!.quantity--
                holder.tvQuantity.text = cart!!.quantity.toString()

                cartPage.updateTotalPayment()
            }
//            notifyItemRangeRemoved(position,carts.size)
            notifyDataSetChanged()
            cartPage.checkButton()
        })
    }
}