package edu.bluejack19_2.HelepDoc.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.DoctorAdapter
import edu.bluejack19_2.HelepDoc.models.Doctor
import java.util.*

class DoctorActivity : AppCompatActivity() {
    private lateinit var rvDoctor: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var doctors:Vector<Doctor>
    val doctorRef =  FirebaseDatabase.getInstance().getReference("doctors")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        rvDoctor = findViewById(R.id.rv_doctor)
        doctors = Vector()
        doctorAdapter =
            DoctorAdapter(doctors, this)

//        initDoctor()

        doctorRef.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(dataSnapShot: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                updateView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val doc = p0.getValue(Doctor::class.java)
                doctors.set(getIndex(doc as Doctor), doc)
                updateView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val doc = p0.getValue(Doctor::class.java)
                doctors.add(doc!!)
                updateView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val doc = p0.getValue(Doctor::class.java)
                doctors.removeAt(getIndex(doc as Doctor))
                updateView()
            }

        })

    }

    private fun getIndex(doc: Doctor): Int {
        var i = 0
        for(d in doctors) {
            if(d.equals(doc)) return i
            i++
        }
        return 0
    }

    private fun updateView() {
        rvDoctor.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@DoctorActivity, 2)
            adapter = doctorAdapter
        }
    }

    private fun initDoctor() {
        var id:String? = doctorRef.push().key
        doctorRef.child(id!!).setValue(Doctor(id, "Hendra Suhendra", "hendra@gmail.com", "081234567890","asdf1234", "Male", "Dentist", 150000f, ArrayList()))
        id = doctorRef.push().key
        doctorRef.child(id!!).setValue(Doctor(id, "Siti Sutarti", "siti@gmail.com", "081234567891", "asdf1234", "Female", "General", 50000f, ArrayList()))
        id = doctorRef.push().key
        doctorRef.child(id!!).setValue(Doctor(id, "Hani Ayunda", "hani@gmail.com", "081234567892", "asdf1234", "Female", "Skin Care", 120000f, ArrayList()))
        id = doctorRef.push().key
        doctorRef.child(id!!).setValue(Doctor(id, "Malik Sumalik", "malik@gmail.com", "081234567894", "asdf1234", "Male", "General", 60000f, ArrayList()))

    }
}
