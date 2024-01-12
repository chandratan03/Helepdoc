package edu.bluejack19_2.HelepDoc.notifications

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseIdService: FirebaseInstanceIdService() {
    var USERID: String? = null
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        USERID = sp.getString("id", "")

//        val firebaseUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val refreshToken = FirebaseInstanceId.getInstance().getToken()
        if(USERID !=null ||  USERID != ""){
            updateToken(refreshToken)
        }
    }

    fun updateToken(refreshToken: String?){
        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        USERID = sp.getString("id", "")
//        val firebaseUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tokens")
        val token:Token = Token(refreshToken)
        reference.child(USERID!!).setValue(token)
    }

}