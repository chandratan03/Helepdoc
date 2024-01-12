package edu.bluejack19_2.HelepDoc.adapters

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.ProductViewHolder
import edu.bluejack19_2.HelepDoc.models.Product
import kotlinx.android.synthetic.main.doctor_dialog.view.*
import java.util.*

class ProductRecommendAdapter(val products: Vector<Product>, val cont: Context): RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.products_recommend, parent, false)
        return ProductViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        var pro = products[position]
        holder.bindItem(pro)
    }

}