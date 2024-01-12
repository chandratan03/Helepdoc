package edu.bluejack19_2.HelepDoc.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.DoctorListViewHolder
import edu.bluejack19_2.HelepDoc.adapters.viewHolders.DoctorViewHolder
import edu.bluejack19_2.HelepDoc.models.Doctor
import kotlinx.android.synthetic.main.activity_register.view.*
import kotlinx.android.synthetic.main.doctor_dialog.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*



class DoctorAdapter(val doctors: Vector<Doctor>, val cont: Context): RecyclerView.Adapter<DoctorListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_card, parent, false)
        return DoctorListViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    override fun onBindViewHolder(holder: DoctorListViewHolder, position: Int) {
        val doctor: Doctor = doctors[position]
        if(doctor.picture.equals(""))
            holder.ivDoctorPicture.setImageResource(R.drawable.doctor)
        else
            holder.ivDoctorPicture.setImageURI(Uri.parse(doctor.picture))
        holder.tvDoctorFullname.text = "Fullname: " + doctor.fullname
        holder.tvDoctorSpecialist.text = "Specialist: " + doctor.specialist
        if(doctor.count_rate != 0) {
            var rate: Float = Math.round(doctor.rating/doctor.count_rate * 10f) / 10f
            holder.tvDoctorRating.text = "Rating: " + (rate).toString() + " stars (" + doctor.count_rate + " review(s))"
        }
        else {
            holder.tvDoctorRating.text = "Rating: NaN"
        }
        holder.tvDoctorFee.text = "Fee: IDR " + doctor.fee.toString()

        holder.itemView.setOnClickListener {
            var sp = cont.getSharedPreferences("Auth",
                Context.MODE_PRIVATE);

            var dialog = LayoutInflater.from(cont).inflate(R.layout.doctor_dialog, null)
            var builder = AlertDialog.Builder(cont).setView(dialog)
            var rbRatingBar: RatingBar = dialog.findViewById(R.id.doctor_star)
            var tvRatingText: TextView = dialog.findViewById(R.id.tv_text_rating)
            var returnBtn: Button = dialog.findViewById(R.id.return_from_dialog)
            for(x in doctor.rated_by) {
                if(sp.getString("id","").equals(x)) {
                    rbRatingBar.visibility = View.GONE
                    tvRatingText.visibility = View.GONE
                    returnBtn.setText("Return")
                    break
                }
            }
            var alert = builder.show()
            if(doctor.picture.equals(""))
                dialog.doctor_picture.setImageResource(R.drawable.doctor)
            else
                dialog.doctor_picture.setImageURI(Uri.parse(doctor.picture))
            dialog.doctor_fullname.text = "Fullname: " + doctor.fullname
            dialog.doctor_gender.text = "Gender: " + doctor.gender
            dialog.doctor_specialist.text = "Specialist: " + doctor.specialist
            dialog.doctor_fee.text = "Fee: IDR " + doctor.fee
            dialog.doctor_email.text = "Email: " + doctor.email
            dialog.doctor_phoneNumber.text = "Phone Number: " + doctor.phoneNumber
            if(doctor.count_rate != 0) {
                var rate: Float = Math.round(doctor.rating/doctor.count_rate * 10f) / 10f
                dialog.doctor_rating.text = "Rating: " + (rate).toString() + " stars (" + doctor.count_rate + " review(s))"
            }
            else {
                dialog.doctor_rating.text = "Rating: NaN"
            }
            dialog.return_from_dialog.setOnClickListener {
                alert.dismiss()
                if(dialog.doctor_star.rating != 0f) {
                    doctor.rated_by.add(sp.getString("id","")!!)
                    var doctorRef = FirebaseDatabase.getInstance().getReference("doctors")
                    doctor.rating += dialog.doctor_star.rating
                    doctor.count_rate++
                    doctorRef.child(doctor.id as String).child("rating").setValue(doctor.rating)
                    doctorRef.child(doctor.id as String).child("count_rate").setValue(doctor.count_rate)
                    doctorRef.child(doctor.id as String).child("rated_by").setValue(doctor.rated_by)
                }
            }
        }
    }

}
