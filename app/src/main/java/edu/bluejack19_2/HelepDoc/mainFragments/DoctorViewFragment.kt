package edu.bluejack19_2.HelepDoc.mainFragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.DoctorAdapter
import edu.bluejack19_2.HelepDoc.models.Doctor

import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoctorViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoctorViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var rvDoctor: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private var doctors = Vector<Doctor>()
    val doctorRef =  FirebaseDatabase.getInstance().getReference("doctors")

    lateinit var cont: Context

    lateinit var act: Activity

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        act = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_doctor_view, container, false)

        cont = this.context!!
        rvDoctor = view.findViewById(R.id.rv_doctor)
        doctorAdapter =
            DoctorAdapter(doctors, cont)

//        initDoctor()

        doctorRef.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(dataSnapShot: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                updateView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                val doc = p0.getValue(Doctor::class.java)
//                doctors.set(getIndex(doc as Doctor), doc)
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

        return view
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
            layoutManager = GridLayoutManager(cont, 2)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoctorViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoctorViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
