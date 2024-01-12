package edu.bluejack19_2.HelepDoc.adapters.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack19_2.HelepDoc.R

class LatestChatViewHolder(view:View):RecyclerView.ViewHolder(view){
    var tvName:TextView = view.findViewById(R.id.tv_name)
    var tvMessage:TextView = view.findViewById(R.id.tv_message)
    var tvDate:TextView =view.findViewById(R.id.tv_date)
    var ivImage:ImageView = view.findViewById(R.id.iv_image)



}