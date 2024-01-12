package edu.bluejack19_2.HelepDoc.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.bluejack19_2.HelepDoc.ChatDetailActivity

class MyFirebaseMessaging: FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        val sented= p0!!.data.get("sented")

        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        val USERID = sp.getString("id", "")
//        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if(USERID !=null && sented.equals(USERID))
            sendNotification(p0)
    }
    private fun sendNotification(rm: RemoteMessage){
        val user = rm.data.get("user")
        val title = rm.data.get("title")
        val body = rm.data.get("body")

        val notification = rm.notification
        val j =Integer.parseInt(user!!.trim())

        val intent = Intent(this, ChatDetailActivity::class.java)

        val bundle = Bundle()

        bundle.putString("userId", user)
        intent.putExtras(bundle)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent:PendingIntent = PendingIntent.getActivity(this, j,intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this).
            setContentTitle(title).
                setContentText(body).
            setAutoCancel(true).
            setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i:Int?=0;
        if (j>0){
            i=j
        }
        noti.notify(i!!, builder.build())









    }
}