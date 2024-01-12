package edu.bluejack19_2.HelepDoc.mainFragments

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.auth.LoginActivity
import edu.bluejack19_2.HelepDoc.models.User
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.change_pass_dialog.view.*
import kotlinx.android.synthetic.main.yes_or_no.view.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
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

    var userRef = FirebaseDatabase.getInstance().getReference("users")
    var cal = Calendar.getInstance()
    var users: Vector<User> = Vector()

    lateinit var act: Activity
    lateinit var cont: Context

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        act = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        var sp = act.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        cont = this.context!!
        initField(view)

        view.findViewById<TextView>(R.id.user_balance).setText("Your Balance: " + sp.getFloat("balance", 0f))

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for(temp in p0.children) {
                        val user = temp.getValue(User::class.java)
                        users.add(user!!)
                    }
                }
            }
        })

//        change_profile_picture.setOnClickListener {
//
//        }

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        view.findViewById<Button>(R.id.edit_bornDate)!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(cont,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        view.findViewById<Button>(R.id.update_profile).setOnClickListener {
            var fullname: String = edit_fullname.text.toString()
            var gender: String = view.findViewById<RadioButton>(view.findViewById<RadioGroup>(R.id.edit_gender).getCheckedRadioButtonId()).text.toString()
            var dob: String = edit_bornDate.text.toString()
            var email: String = edit_email.text.toString()
            var phoneNumber: String = edit_phone.text.toString()

            if(fullname.equals("") || gender.equals("") || dob.equals("Change Date") || email.equals("") || phoneNumber.equals("")) {
                Toast.makeText(cont, "Please input all field!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            for(u in users) {
                if(u.id.equals(sp.getString("id",""))) continue
                if(u.email.equals(email)) {
                    Toast.makeText(cont, "Email already used by other user!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else if(u.phoneNumber.equals(phoneNumber)) {
                    Toast.makeText(cont, "Phone Number already used by other user!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            var dialog = LayoutInflater.from(cont).inflate(R.layout.yes_or_no, null)
            var builder = AlertDialog.Builder(cont).setView(dialog)
            var alert = builder.show()

            dialog.yesBtn.setOnClickListener {
                updateProfile(fullname, dob, email, phoneNumber, gender)
                Toast.makeText(cont, "Profile successfully updated!", Toast.LENGTH_LONG).show()
                alert.dismiss()
            }

            dialog.noBtn.setOnClickListener {
                alert.dismiss()
            }

        }

        view.findViewById<Button>(R.id.change_password).setOnClickListener{
            var dialog = LayoutInflater.from(cont).inflate(R.layout.change_pass_dialog, null)
            var builder = AlertDialog.Builder(cont).setView(dialog)
            var alert = builder.show()

            dialog.change_password.setOnClickListener {
                var oldPass: String? = dialog.old_password.text.toString()
                var newPass: String? = dialog.new_password.text.toString()
                var confNewPass: String? = dialog.confirm_new_password.text.toString()

                if(!sp.getString("password","").equals(oldPass)) {
                    Toast.makeText(cont, "Old password doesn't match!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else if(!newPass.equals(confNewPass)) {
                    Toast.makeText(cont, "New password doesn't match with confirm new password!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else
                {
                    userRef.child(sp.getString("id", "") as String).child("password").setValue(newPass)
                    val auth = sp.edit()
                    auth.putString("password", newPass)
                    auth.commit()
                    Toast.makeText(cont, "Password successfully updated!", Toast.LENGTH_LONG).show()
                }
                alert.dismiss()
            }

            dialog.cancel_change_password.setOnClickListener {
                alert.dismiss()
            }
        }

        return view
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        edit_bornDate!!.text = sdf.format(cal.getTime())
    }

    private fun initField(view: View) {
        var sp = act.getSharedPreferences("Auth", Context.MODE_PRIVATE);

        view.findViewById<EditText>(R.id.edit_fullname).setText(sp.getString("fullname", ""))
        if(sp.getString("gender", "").equals("Male"))
            view.findViewById<RadioButton>(R.id.edit_radioMale).isChecked = true
        else
            view.findViewById<RadioButton>(R.id.edit_radioFemale).isChecked = true
        view.findViewById<Button>(R.id.edit_bornDate).setText(sp.getString("dob", ""))
        view.findViewById<EditText>(R.id.edit_email).setText(sp.getString("email", ""))
        view.findViewById<EditText>(R.id.edit_phone).setText(sp.getString("phone", ""))
    }

    private fun updateProfile(fullname: String, dob: String, email: String, phoneNumber: String, gender: String) {
        var sp = act.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        userRef.child(sp.getString("id", "") as String).child("fullname").setValue(fullname)
        userRef.child(sp.getString("id", "") as String).child("dob").setValue(dob)
        userRef.child(sp.getString("id", "") as String).child("email").setValue(email)
        userRef.child(sp.getString("id", "") as String).child("phoneNumber").setValue(phoneNumber)
        userRef.child(sp.getString("id", "") as String).child("gender").setValue(gender)
        val auth = sp.edit()
        auth.putString("fullname", fullname)
        auth.putString("dob", dob)
        auth.putString("email", email)
        auth.putString("phone", phoneNumber)
        auth.putString("gender", gender)
        auth.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
