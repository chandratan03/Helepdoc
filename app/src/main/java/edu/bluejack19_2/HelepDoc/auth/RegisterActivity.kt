package edu.bluejack19_2.HelepDoc.auth

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.User
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    var userRef = FirebaseDatabase.getInstance().getReference("users")
    var users: Vector<User> = Vector()
    var cal = Calendar.getInstance()
    lateinit var genderRadio : RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        genderRadio = findViewById<RadioGroup>(R.id.genderGroup)

        var sp = getSharedPreferences("Auth",
            Context.MODE_PRIVATE);

        if(sp.getString("comeFrom", "").equals("LoginNoGmailAccount")) {
            findViewById<EditText>(R.id.fullname).setText(sp.getString("fullname", ""))
            findViewById<EditText>(R.id.email).setText(sp.getString("email", ""))
            findViewById<EditText>(R.id.phone).setText(sp.getString("phone", ""))
        }

        resetSP()

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

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        changeDate!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@RegisterActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        gotoSignin.setOnClickListener {
            goToSignIn()
        }

        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)

        register.setOnClickListener {
            var fullname = findViewById<EditText>(R.id.fullname).text.toString()
            var email = findViewById<EditText>(R.id.email).text.toString()
            var password = findViewById<EditText>(R.id.password).text.toString()
            var confirmpass = findViewById<EditText>(R.id.confirmpass).text.toString()
            var phone = findViewById<EditText>(R.id.phone).text.toString()
            var gender = ""
            var dob = changeDate.text.toString()

            try {
                gender = findViewById<RadioButton>(findViewById<RadioGroup>(R.id.genderGroup).getCheckedRadioButtonId()).text.toString()
            }
            catch (e : Exception){
                Toast.makeText(applicationContext, "Please fill all field!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(fullname.equals("") || dob.equals("Change Date") || email.equals("") || password.equals("") || confirmpass.equals("") || phone.equals("") || gender.equals("")) {
                Toast.makeText(applicationContext, "Please fill all field!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!checkEmail(email)) {
                Toast.makeText(applicationContext, "Email must Gmail or Yahoo Mail format!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!checkPhoneNumber(phone)) {
                Toast.makeText(this, "Phone must consist of 10 - 12 digit!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!checkPass(password)) {
                Toast.makeText(this, "Password must consist of 8 alphanumeric or more!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            for(u in users) {
                if(email.equals(u.email) || phone.equals(u.phoneNumber)) {
                    Toast.makeText(applicationContext, "User already exists!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            if(!confirmpass.equals(password)) {
                Toast.makeText(applicationContext, "Password not match with confirm password!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var id: String? = userRef.push().key
            userRef.child(id!!).setValue(User(id, fullname, gender, dob, email, password, phone))

            Toast.makeText(applicationContext, "Successfully registered user!", Toast.LENGTH_LONG).show()
            resetField()
            setSP(id, fullname, email, password, phone, gender, dob)

            goToSignIn()
        }

    }

    private fun resetField() {
        genderRadio.clearCheck()
        changeDate.text = "Change Date"
        findViewById<EditText>(R.id.fullname).setText("")
        findViewById<EditText>(R.id.email).setText("")
        findViewById<EditText>(R.id.password).setText("")
        findViewById<EditText>(R.id.confirmpass).setText("")
        findViewById<EditText>(R.id.phone).setText("")
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        changeDate!!.text = sdf.format(cal.getTime())
    }

    private fun setSP(id: String, fullname: String, email: String, password: String, phone: String, gender: String, dob: String) {
        val sp = getSharedPreferences("Auth", Context.MODE_PRIVATE)

        val auth = sp.edit()

        auth.putString("id", id)
        auth.putString("fullname", fullname)
        auth.putString("email", email)
        auth.putString("password", password)
        auth.putString("phone", phone)
        auth.putString("gender", gender)
        auth.putString("dob", dob)
        auth.putFloat("balance", 0f)
        auth.putString("comeFrom", "Register")

        auth.commit()
    }

    private fun resetSP() {
        val sp = getSharedPreferences("Auth", Context.MODE_PRIVATE)

        val auth = sp.edit()

        auth.putString("id", "")
        auth.putString("fullname", "")
        auth.putString("email", "")
        auth.putString("password", "")
        auth.putString("phone", "")
        auth.putString("gender", "")
        auth.putString("dob", "")
        auth.putFloat("balance", 0.0f)
        auth.putString("comeFrom", "")

        auth.commit()
    }

    fun goToSignIn() {
        var intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener { result ->
            var fullname = findViewById<EditText>(R.id.fullname).setText(result.user!!.displayName)
            var email = findViewById<EditText>(R.id.email).setText(result.user!!.email)
            var phone = findViewById<EditText>(R.id.phone).setText(result.user!!.phoneNumber)
            return@addOnSuccessListener
        }
    }

    private fun checkEmail(email: String): Boolean {
        if((!email.endsWith("@gmail.com") && !email.endsWith("@yahoo.com")) || email.startsWith("@gmail.com") || email.startsWith("@yahoo.com")) {
            return false
        }
        return true
    }

    private fun checkPass(pass: String): Boolean {
        var alpha = 0
        var num = 0
        if(pass.length < 8) {
            return false
        }
        for (x in pass) {
            if(x.isLetter()) alpha++
            else if(x.isDigit()) num++
            else return false
        }
        if(alpha == 0 || num == 0) return false
        return true
    }

    private fun checkPhoneNumber(phone: String): Boolean {
        for(x in phone) {
            if(!x.isDigit()) return false
        }
        if(phone.length < 10 || phone.length > 12) return false
        return true
    }
}
