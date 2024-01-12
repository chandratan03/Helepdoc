package edu.bluejack19_2.HelepDoc.adapters

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.DoctorViewHolder
import edu.bluejack19_2.HelepDoc.models.Doctor

import kotlinx.android.synthetic.main.doctor_dialog.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class DoctorRecommendAdapter(val doctors: Vector<Doctor>, val cont: Context): RecyclerView.Adapter<DoctorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_recommend, parent, false)
        return DoctorViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        var doc = doctors[position]
        if(!doc.picture.equals(""))
            holder.ivDoctorPicture.setImageURI(Uri.parse(doc.picture))
        else
            holder.ivDoctorPicture.setImageResource(R.drawable.doctor)
        holder.tvDoctorFullname.setText("Fullname: " + doc.fullname)
        holder.tvDoctorSpecialist.setText("Specialist: " + doc.specialist)
        holder.tvDoctorFee.setText("Fee " + doc.fee)
        if(doc.count_rate != 0) {
            var rate: Float = Math.round(doc.rating/doc.count_rate * 10f) / 10f
            holder.tvDoctorRating.setText("Rating: " + (rate).toString() + " stars (" + doc.count_rate + ")")
        }
        else {
            holder.tvDoctorRating.setText("Rating: NaN")
        }

//        holder.itemView.setOnClickListener {
//            var dialog = LayoutInflater.from(cont).inflate(R.layout.doctor_dialog, null)
//            var builder = AlertDialog.Builder(cont).setView(dialog)
//            var alert = builder.show()
//            if(doc.picture.equals(""))
//                dialog.doctor_picture.setImageResource(R.drawable.doctor)
//            else
//                dialog.doctor_picture.setImageURI(Uri.parse(doc.picture))
//            dialog.doctor_fullname.text = "Fullname: " + doc.fullname
//            dialog.doctor_gender.text = "Gender: " + doc.gender
//            dialog.doctor_specialist.text = "Specialist: " + doc.specialist
//            dialog.doctor_fee.text = "Fee: IDR " + doc.fee
//            dialog.doctor_email.text = "Email: " + doc.email
//            dialog.doctor_phoneNumber.text = "Phone Number: " + doc.phoneNumber
//            var rate = Math.round(doc.rating/doc.count_rate * 10) / 10
//            dialog.doctor_rating.text = "Rating: " + (rate).toString() + " stars (" + doc.count_rate + " review(s))"
//            dialog.return_from_dialog.setOnClickListener {
//                alert.dismiss()
//                if(dialog.doctor_star.rating != 0f) {
//                    var doctorRef = FirebaseDatabase.getInstance().getReference("doctors")
//                    doc.rating += dialog.doctor_star.rating
//                    doc.count_rate++
//                    doctorRef.child(doc.id as String).child("rating").setValue(doc.rating)
//                    doctorRef.child(doc.id as String).child("count_rate").setValue(doc.count_rate)
//                }
//            }
//        }
    }
}