package edu.bluejack19_2.HelepDoc.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack19_2.HelepDoc.ChatDetailActivity
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Doctor

class DoctorAdapterF(val doctors:ArrayList<Doctor>, val categoryNames:ArrayList<String>,val currId:String, val context:Context): RecyclerView.Adapter<DoctorFViewHolder>(), Filterable{

    var DOCTORS:ArrayList<Doctor>? = ArrayList()
    var currCategory:String = "all"
    init{
        DOCTORS?.addAll(doctors)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorFViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.doctor_f, parent, false)
        return DoctorFViewHolder(view)
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    override fun onBindViewHolder(holder: DoctorFViewHolder, position: Int) {
        val doctor:Doctor = doctors[position]
        holder.ivDoctor.setImageResource(R.drawable.doctor)
        holder.tvName.text = doctor.fullname
        holder.tvSpecialist.text = doctor.specialist
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ChatDetailActivity::class.java)
            intent.putExtra("CURRENTID", currId)
            intent.putExtra("OPPONENTID", doctor.id)
            context.startActivity(intent)
        })
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString:String = constraint.toString()
                var doctorList:ArrayList<Doctor> = ArrayList()
                if(charString.isEmpty()){

                    if(currCategory.equals("all")){
                        DOCTORS?.let { doctorList.addAll(it) }
                    }else{
                        for(d in DOCTORS!!){
                            if(d.specialist.toLowerCase().equals(currCategory.toLowerCase())){
                                doctorList.add(d)
                            }
                        }
                    }
                }else{
                    for(d in DOCTORS!!){
                        if(currCategory.equals("all")){
                            if(d.fullname.toLowerCase().contains(charString.toLowerCase())){
                                doctorList.add(d)
                            }
                        }else{
                            if(d.fullname.toLowerCase().contains(charString.toLowerCase()) &&
                                d.specialist.toLowerCase().equals(currCategory.toLowerCase())){
                                doctorList.add(d)
                            }
                        }
                    }

                }
                var filterResults = FilterResults()
                filterResults.values = doctorList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                doctors.removeAll(doctors)
                doctors.addAll(results?.values as ArrayList<Doctor>)
                notifyDataSetChanged()

            }

        }
    }


}