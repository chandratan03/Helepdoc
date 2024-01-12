package edu.bluejack19_2.HelepDoc.chatFragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.DoctorAdapterF
import edu.bluejack19_2.HelepDoc.models.Doctor


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoctorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoctorFragment : Fragment() {
    // TODO: Rename and change types of parameters

    //default
    private var param1: String? = null
    private var param2: String? = null

    // CHANGE THIS ONLY
    private var CURRENTID:String? = "-M43c_mp8Ur1bDV3PksP" // USER


    // ours
    private var rvDoctor:RecyclerView? = null
    private lateinit var doctorAdapterF: DoctorAdapterF
    private lateinit var doctors:ArrayList<Doctor>
    private lateinit var svDoctor:SearchView
    private lateinit var spinnerCategory: Spinner
    private lateinit var categoryNames: ArrayList<String>
    private lateinit var spinnerAdapter:ArrayAdapter<String>

    val doctorRef =  FirebaseDatabase.getInstance().getReference("doctors")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_doctor, container, false)
        var sp = view.context.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        CURRENTID = sp.getString("id", "")
        svDoctor = view.findViewById(R.id.sv_doctors)
        rvDoctor = view.findViewById(R.id.rv_doctor)
        doctors = ArrayList()
        spinnerCategory = view.findViewById(R.id.spinner_category)
        categoryNames = ArrayList()
        categoryNames.add("all")

        doctorRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(view.context, "Fail to fetch doctors", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(d in p0.children){
                        val doctor = d.getValue(Doctor::class.java)
                        doctors.add(doctor!!)
                    }
                    for(doctor in doctors){
                        var isExist:Boolean = false
                        for(category in categoryNames){
                            if(category.toLowerCase().equals(doctor.specialist.toLowerCase())){
                                isExist = true
                                break
                            }
                        }
                        if(!isExist){
                            categoryNames.add(doctor.specialist)
                        }
                    }
//                    spinnerCategory.
                    spinnerAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, categoryNames)
                    spinnerCategory.adapter = spinnerAdapter
                    doctorAdapterF =
                        DoctorAdapterF(
                            doctors,
                            categoryNames,
                            CURRENTID!!,
                            view.context
                        )

                    rvDoctor?.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(view.context)
                        adapter = doctorAdapterF

                    }
                    view.findViewById<RelativeLayout>(R.id.loading).visibility = View.GONE
//                    val parentFragment2  = (getParentFragment() as NavHostFragment).parentFragment as MainChatFragment
//                    parentFragment2.removeLoading()
                }
            }
        })
        svDoctor.isIconifiedByDefault = false
        svDoctor.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                doctorAdapterF.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                doctorAdapterF.filter.filter(newText)
                return false
            }

        })
        spinnerCategory.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                doctorAdapterF.currCategory = categoryNames[position]
                doctorAdapterF.filter.filter("")
             }

        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoctorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoctorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
