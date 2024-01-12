package edu.bluejack19_2.HelepDoc.adapters.viewHolders

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack19_2.HelepDoc.R

class DoctorListViewHolder constructor (view: View): RecyclerView.ViewHolder(view) {
    var ivDoctorPicture: ImageView = view.findViewById(R.id.doctor_picture) as ImageView
    var tvDoctorFullname: TextView = view.findViewById(R.id.doctor_fullname)
    var tvDoctorFee: TextView = view.findViewById(R.id.doctor_fee)
    var tvDoctorSpecialist: TextView = view.findViewById(R.id.doctor_specialist)
    var tvDoctorRating: TextView = view.findViewById(R.id.doctor_rating)
    var v = view
}