package edu.bluejack19_2.HelepDoc.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.bluejack19_2.HelepDoc.NavigatorActivity
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Doctor
import edu.bluejack19_2.HelepDoc.models.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    var userRef = FirebaseDatabase.getInstance().getReference("users")
    var doctorRef = FirebaseDatabase.getInstance().getReference("doctors")
    var users: ArrayList<User> = ArrayList()
    var doctors: ArrayList<Doctor> = ArrayList()
    lateinit var roleRadio : RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        roleRadio = findViewById<RadioGroup>(R.id.roleGroup)

        firebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
        var sp = getSharedPreferences("Auth",
            Context.MODE_PRIVATE);

        var email = sp.getString("email", "");
        findViewById<EditText>(R.id.emailOrPhone).setText(email)

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

        doctorRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for(temp in p0.children) {
                        val doctor = temp.getValue(Doctor::class.java)
                        doctors.add(doctor!!)
                    }
                }
            }
        })

        login.setOnClickListener {
            var emailOrPhone = findViewById<EditText>(R.id.emailOrPhone).text.toString()
            var password = findViewById<EditText>(R.id.password).text.toString()
            var role = ""

            if(emailOrPhone.equals("") || password.equals("")) {
                Toast.makeText(applicationContext, "Please fill all field!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                role = findViewById<RadioButton>(findViewById<RadioGroup>(R.id.roleGroup).getCheckedRadioButtonId()).text.toString()
            }
            catch (e : Exception){
                Toast.makeText(applicationContext, "Please fill all field!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(role.equals("User")) {
                for(u in users) {
                    if((emailOrPhone.equals(u.email) || emailOrPhone.equals(u.phoneNumber)) && password.equals(u.password)) {
                        Toast.makeText(applicationContext, "Login success!", Toast.LENGTH_LONG).show()
                        setSP(u.id as String, u.fullname, u.email, u.password, u.phoneNumber, u.gender, u.dob, u.balance, u.picture, "User")
                        var intent = Intent(this@LoginActivity, NavigatorActivity::class.java)
                        finish()
                        startActivity(intent)
                        return@setOnClickListener
                    }
                }
            }
            else if(role.equals("Doctor")) {
                for(d in doctors) {
                    if((emailOrPhone.equals(d.email) || emailOrPhone.equals(d.phoneNumber)) && password.equals(d.password)) {
                        Toast.makeText(applicationContext, "Login success!", Toast.LENGTH_LONG).show()
                        setSP(d.id as String, d.fullname, d.email, d.password, d.phoneNumber, d.gender, "", 0f, "", "Doctor")
                        var intent = Intent(this@LoginActivity, NavigatorActivity::class.java)
                        finish()
                        startActivity(intent)
                        return@setOnClickListener
                    }
                }
            }

            Toast.makeText(applicationContext, "User doesn't exists or wrong password!", Toast.LENGTH_LONG).show()
            findViewById<EditText>(R.id.password).setText("")
        }

        googleSignIn.setOnClickListener {
            signInGoogle()
        }

        Handler().postDelayed(Runnable {
            if(!sp.getString("email", "").equals("")) {
                if(sp.getString("role", "").equals("User")) {
                    for(u in users) {
//                    Toast.makeText(applicationContext, "Toast", Toast.LENGTH_LONG).show()
//                    Log.v(sp.getString("email", "") + " " + u.email + " " + sp.getString("password", "") + " " + u.password, "Test")
                        if((sp.getString("email", "").equals(u.email) || sp.getString("phone", "").equals(u.phoneNumber)) && sp.getString("password", "").equals(u.password)) {
                            Toast.makeText(applicationContext, "Login success!", Toast.LENGTH_LONG).show()
                            setSP(u.id as String, u.fullname, u.email, u.password, u.phoneNumber, u.gender, u.dob, u.balance, u.picture, "User")
                            var intent = Intent(this@LoginActivity, NavigatorActivity::class.java)
                            finish()
                            startActivity(intent)
                        }
                    }
                }
                else if(sp.getString("role", "").equals("Doctor")) {
                    for(d in doctors) {
                        if((sp.getString("email", "").equals(d.email) || sp.getString("phone", "").equals(d.phoneNumber)) && sp.getString("password", "").equals(d.password)) {
                            Toast.makeText(applicationContext, "Login success!", Toast.LENGTH_LONG).show()
                            setSP(d.id as String, d.fullname, d.email, d.password, d.phoneNumber, d.gender, "", 0f, "", "Doctor")
                            var intent = Intent(this@LoginActivity, NavigatorActivity::class.java)
                            finish()
                            startActivity(intent)
                        }
                    }
                }
            }
            loading.visibility = View.GONE
        }, 2000)

    }

    fun goToSignUp(view: View) {
        var intent = Intent(this@LoginActivity,RegisterActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun setSP(id: String, fullname: String, email: String, password: String, phone: String, gender: String, dob: String, balance: Float, picture: String, role: String) {
        val sp = getSharedPreferences(
            "Auth",
            Context.MODE_PRIVATE
        )

        val auth = sp.edit()

        auth.putString("id", id)
        auth.putString("fullname", fullname)
        auth.putString("email", email)
        auth.putString("password", password)
        auth.putString("phone", phone)
        auth.putString("gender", gender)
        auth.putString("dob", dob)
        auth.putFloat("balance", balance)
        auth.putString("picture", picture)
        auth.putString("role", role)

        auth.commit()
    }

    private fun resetSP() {
        val sp = getSharedPreferences(
            "Auth",
            Context.MODE_PRIVATE
        )

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
        auth.putString("role", "")
        auth.putString("comeFrom", "")

        auth.commit()
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

    var nameRes : String? = ""
    var gmailRes : String? = ""
    var phoneRes : String? = ""

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener { result ->
            nameRes = result.user!!.displayName
            gmailRes = result.user!!.email
            phoneRes = result.user!!.phoneNumber
            checkGmailAuth()
            return@addOnSuccessListener
        }
    }

    private fun checkGmailAuth() {
        for(u in users) {
            if(gmailRes!!.toString().equals(u.email)){
                setSP(u.id as String, u.fullname, u.email, u.password, u.phoneNumber, u.gender, u.dob, u.balance, u.picture, "User")
                Toast.makeText(applicationContext, "Login success!", Toast.LENGTH_LONG).show()
                var intent = Intent(this@LoginActivity, NavigatorActivity::class.java)
                finish()
                startActivity(intent)
                return
            }
        }
        val sp = getSharedPreferences(
            "Auth",
            Context.MODE_PRIVATE
        )

        val auth = sp.edit()

        auth.putString("fullname", nameRes)
        auth.putString("email", gmailRes)
        auth.putString("phone", phoneRes)
        auth.putString("comeFrom", "LoginNoGmailAccount")
        auth.commit()


        var id: String? = userRef.push().key
        val calendar  = java.util.Calendar.getInstance().time
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val currentDate = sdf.format(Date())
        userRef.child(id!!).setValue(User(id, nameRes!!, "Male", currentDate.toString(), gmailRes!!, "asdf1234", ""))
        setSP(id as String, nameRes!!, gmailRes!!, "asdf1234", "", "Male", currentDate.toString(), 0f, "", "User")

        var intent = Intent(this@LoginActivity, NavigatorActivity::class.java)
        finish()
        startActivity(intent)
    }
}
