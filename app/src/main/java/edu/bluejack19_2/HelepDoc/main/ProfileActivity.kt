package edu.bluejack19_2.HelepDoc.main

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.auth.LoginActivity
import edu.bluejack19_2.HelepDoc.models.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.change_pass_dialog.view.*
import kotlinx.android.synthetic.main.yes_or_no.view.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    var userRef = FirebaseDatabase.getInstance().getReference("users")
    var cal = Calendar.getInstance()
    var users: Vector<User> = Vector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        initField()

        user_balance.setText("Your Balance: " + sp.getFloat("balance", 0f))

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

        goback.setOnClickListener {
            goToHome()
        }

        logout.setOnClickListener {
            logout()
        }

        change_profile_picture.setOnClickListener {

        }

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        edit_bornDate!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@ProfileActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        update_profile.setOnClickListener {
            var fullname: String = edit_fullname.text.toString()
            var gender: String = findViewById<RadioButton>(findViewById<RadioGroup>(R.id.edit_gender).getCheckedRadioButtonId()).text.toString()
            var dob: String = edit_bornDate.text.toString()
            var email: String = edit_email.text.toString()
            var phoneNumber: String = edit_phone.text.toString()

            if(fullname.equals("") || gender.equals("") || dob.equals("Change Date") || email.equals("") || phoneNumber.equals("")) {
                Toast.makeText(applicationContext, "Please input all field!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            for(u in users) {
                if(u.id.equals(sp.getString("id",""))) continue
                if(u.email.equals(email)) {
                    Toast.makeText(applicationContext, "Email already used by other user!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else if(u.phoneNumber.equals(phoneNumber)) {
                    Toast.makeText(applicationContext, "Phone Number already used by other user!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            var dialog = LayoutInflater.from(this).inflate(R.layout.yes_or_no, null)
            var builder = AlertDialog.Builder(this).setView(dialog)
            var alert = builder.show()

            dialog.yesBtn.setOnClickListener {
                updateProfile(fullname, dob, email, phoneNumber, gender)
                Toast.makeText(applicationContext, "Profile successfully updated!", Toast.LENGTH_LONG).show()
                alert.dismiss()
                goToHome()
            }

            dialog.noBtn.setOnClickListener {
                alert.dismiss()
            }

        }

        change_password.setOnClickListener{
            var dialog = LayoutInflater.from(this).inflate(R.layout.change_pass_dialog, null)
            var builder = AlertDialog.Builder(this).setView(dialog)
            var alert = builder.show()

            dialog.change_password.setOnClickListener {
                var oldPass: String? = dialog.old_password.text.toString()
                var newPass: String? = dialog.new_password.text.toString()
                var confNewPass: String? = dialog.confirm_new_password.text.toString()

                if(!sp.getString("password","").equals(oldPass)) {
                    Toast.makeText(applicationContext, "Old password doesn't match!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else if(!newPass.equals(confNewPass)) {
                    Toast.makeText(applicationContext, "New password doesn't match with confirm new password!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else
                {
                    userRef.child(sp.getString("id", "") as String).child("password").setValue(newPass)
                    val auth = sp.edit()
                    auth.putString("password", newPass)
                    auth.commit()
                    Toast.makeText(applicationContext, "Password successfully updated!", Toast.LENGTH_LONG).show()
                }
                alert.dismiss()
            }

            dialog.cancel_change_password.setOnClickListener {
                alert.dismiss()
            }
        }

        view_all_transaction.setOnClickListener {
            var intent = Intent(this@ProfileActivity, ViewTransactionActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        edit_bornDate!!.text = sdf.format(cal.getTime())
    }

    private fun initField() {
        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);

        edit_fullname.setText(sp.getString("fullname", ""))
        if(sp.getString("gender", "").equals("Male"))
            edit_radioMale.isChecked = true
        else
            edit_radioFemale.isChecked = true
        edit_bornDate.setText(sp.getString("dob", ""))
        edit_email.setText(sp.getString("email", ""))
        edit_phone.setText(sp.getString("phone", ""))
    }

    private fun updateProfile(fullname: String, dob: String, email: String, phoneNumber: String, gender: String) {
        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);
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

    private fun logout() {
        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val auth = sp.edit()
        auth.putString("id", "")
        auth.putString("fullname", "")
        auth.putString("email", "")
        auth.putString("password", "")
        auth.putString("phone", "")
        auth.putString("gender", "")
        auth.putString("dob", "")
        auth.putFloat("balance", 0.0f)
        auth.putString("picture", "")
        auth.putString("comeFrom", "")
        auth.commit()
        var intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun goToHome() {
        var intent = Intent(this@ProfileActivity, HomeActivity::class.java)
        finish()
        startActivity(intent)
    }
}
