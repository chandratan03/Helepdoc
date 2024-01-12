package edu.bluejack19_2.HelepDoc.adapters.viewHolders


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack19_2.HelepDoc.R

class ChatRightViewHolder (view:View):RecyclerView.ViewHolder(view){
    var tvMessage:TextView = view.findViewById(R.id.tv_message)

    fun bindItem(message:String?){
        tvMessage.text = message
    }

}