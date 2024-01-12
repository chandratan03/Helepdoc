package edu.bluejack19_2.HelepDoc.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.TransactionViewHolder
import edu.bluejack19_2.HelepDoc.models.Cart
import edu.bluejack19_2.HelepDoc.models.Transaction

class TransactionAdapter(var transactions: ArrayList<Transaction>, var cont: Context): RecyclerView.Adapter<TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_view, parent, false)
        return TransactionViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        var trans = transactions[position]
        holder.bindItem(trans, cont)
        holder.rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(cont, LinearLayoutManager.HORIZONTAL, false)
            adapter =
                TransactionDetailAdapter(trans.carts as ArrayList<Cart>)
        }
//        holder.layout.setOnClickListener {
//            if(holder.detail_layout.visibility == View.GONE) {
//                holder.detail_layout.visibility = View.VISIBLE
//                holder.dropBtn.visibility = View.GONE
//                holder.dropRevBtn.visibility = View.VISIBLE
//            }
//            else if(holder.detail_layout.visibility == View.VISIBLE) {
//                holder.detail_layout.visibility = View.GONE
//                holder.dropRevBtn.visibility = View.GONE
//                holder.dropBtn.visibility = View.VISIBLE
//            }
//        }
        holder.dropBtn.setOnClickListener {
            holder.detail_layout.visibility = View.VISIBLE
            holder.dropBtn.visibility = View.GONE
            holder.dropRevBtn.visibility = View.VISIBLE
        }
        holder.dropRevBtn.setOnClickListener {
            holder.detail_layout.visibility = View.GONE
            holder.dropRevBtn.visibility = View.GONE
            holder.dropBtn.visibility = View.VISIBLE
        }
    }
}