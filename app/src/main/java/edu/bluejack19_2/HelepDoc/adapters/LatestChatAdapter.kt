package edu.bluejack19_2.HelepDoc.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import edu.bluejack19_2.HelepDoc.ChatDetailActivity
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.LatestChatViewHolder
import edu.bluejack19_2.HelepDoc.models.Chat
import edu.bluejack19_2.HelepDoc.models.Doctor
import edu.bluejack19_2.HelepDoc.models.User
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class LatestChatAdapter(val chats:ArrayList<Chat>, val currId:String,val context:Context): RecyclerView.Adapter<LatestChatViewHolder>() {
    private var doctorReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("doctors")
    private var userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var formatter:SimpleDateFormat=SimpleDateFormat("dd/MM/yyyy")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestChatViewHolder {
        val view: View= LayoutInflater.from(parent.context).inflate(R.layout.latest_chat_item, parent,false)
        return LatestChatViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: LatestChatViewHolder, position: Int) {
        var chat:Chat = chats[position]
        userReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.hasChild(currId)){
                    readUserChild(holder,  chat)
                }
            }
        })

        doctorReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.hasChild(currId)){
                    readDoctorChild(holder, chat)
                }
            }

        })
    }

    fun readUserChild(holder: LatestChatViewHolder, chat:Chat){
        Log.i("USER", chat.userId.toString())
        userReference.child(chat.userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    val user: User? = p0.getValue(User::class.java)

                    Log.i("HEHE", "aaa"+p0.getValue().toString())
                    holder.ivImage.setImageResource(R.drawable.doctor)
                    holder.tvName.text = user!!.fullname
                    if (chat.messages!!.size > 0){
                        holder.tvMessage.text = chat.messages!![chat.messages!!.size - 1].message.toString()
                        val day:String = formatter.format(chat.messages!![chat.messages!!.size - 1].date)
                        holder.tvDate.text = day

                        holder.itemView.setOnClickListener(View.OnClickListener {
                            val intent = Intent(context, ChatDetailActivity::class.java)
                            intent.putExtra("CURRENTID", currId)
                            intent.putExtra("OPPONENTID", user.id)
                            context.startActivity(intent)
                        })
                    }else{
                        holder.tvMessage.text = ""
                        holder.itemView.setOnClickListener(View.OnClickListener {
                            val intent = Intent(context, ChatDetailActivity::class.java)
                            intent.putExtra("CURRENTID", currId)
                            intent.putExtra("OPPONENTID", user.id)
                            context.startActivity(intent)
                        })
                    }
                }
                Log.i("HORE", "HORE")
            }

        })
    }

    fun readDoctorChild(holder: LatestChatViewHolder, chat:Chat){
        Log.i("DOCTOR", chat.doctorId.toString())
        doctorReference.child(chat.doctorId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    val doctor: Doctor? = p0.getValue(Doctor::class.java)
                    holder.tvName.text = doctor!!.fullname
                    holder.ivImage.setImageResource(R.drawable.doctor)
                    Log.i("HEHE", "aaa"+p0.getValue().toString())
                    if (chat.messages!!.size > 0){
                        holder.tvMessage.text = chat.messages!![chat.messages!!.size - 1].message.toString()
                        val day:String = formatter.format(chat.messages!![chat.messages!!.size - 1].date)
                        holder.tvDate.text = day

                        holder.itemView.setOnClickListener(View.OnClickListener {
                            val intent = Intent(context,ChatDetailActivity::class.java)
                            intent.putExtra("CURRENTID", currId)
                            intent.putExtra("OPPONENTID", doctor.id)
                            context.startActivity(intent)
                        })

                    }else{
                        holder.tvMessage.text = ""
                        holder.itemView.setOnClickListener(View.OnClickListener {
                            val intent = Intent(context, ChatDetailActivity::class.java)
                            intent.putExtra("CURRENTID", currId)
                            intent.putExtra("OPPONENTID", doctor.id)
                            context.startActivity(intent)
                        })
                    }

                }

                Log.i("HORE", "HORE")
            }

        })
    }



}