package edu.bluejack19_2.HelepDoc

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import edu.bluejack19_2.HelepDoc.adapters.ChatDetailAdapter
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Chat
import edu.bluejack19_2.HelepDoc.models.Doctor
import edu.bluejack19_2.HelepDoc.models.Message
import edu.bluejack19_2.HelepDoc.models.User
import edu.bluejack19_2.HelepDoc.notifications.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var toolBar: Toolbar

//    private val CURRENTID:String = "-M48nlk7aItLyYcj_b5G"  // DOCTOR
//    private val OPPONENTID:String = "-M43c_mp8Ur1bDV3PksP"  // USER

    //Login as user
    private var OPPONENTID:String = "-M48nlk7aItLyYcj_b5G" // DOCTOR
    private var CURRENTID:String = "-M43c_mp8Ur1bDV3PksP" // USER



    private lateinit var opponentName:String

    private lateinit var userReference: DatabaseReference
    private lateinit var doctorReference: DatabaseReference
    private lateinit var chatReference: DatabaseReference
    private lateinit var currChatReference: DatabaseReference
    private lateinit var messagesReference: DatabaseReference
    private lateinit var btnSend: ImageButton
    private lateinit var etMessage: EditText
    private lateinit var rvMessages:RecyclerView
    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private lateinit var chat:Chat
    private lateinit var messages:ArrayList<Message>


    var apiService:APIService? = null
    var notify:Boolean = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)



        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)



        CURRENTID= intent.getStringExtra("CURRENTID").toString()
        OPPONENTID = intent.getStringExtra("OPPONENTID").toString()
        initReferences()
        initComponents()
        initToolbar()

        initObject()

        chatReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var pattern:String
                if(p0.hasChild(CURRENTID+"_"+OPPONENTID)){
                    Log.i("HERE", "1")
                    pattern = CURRENTID+"_"+OPPONENTID
                    currChatReference = chatReference.child(pattern)
                    readChild()
                }else if(p0.hasChild(OPPONENTID+"_"+CURRENTID)){
                    Log.i("HERE", "2")
                    pattern = OPPONENTID+"_"+CURRENTID
                    currChatReference = chatReference.child(pattern)
                    readChild()
                }else{
                    Log.i("HERE", "3")
                    pattern = CURRENTID+"_"+OPPONENTID
                    currChatReference = chatReference.child(pattern)

                    //if login as a doctor and the doctor start the chat
                    Log.i("MYID", CURRENTID)
                    doctorReference.child(CURRENTID).addValueEventListener(object:ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            Log.i("CURRNET", p0.exists().toString())

                            if(p0.exists()){
                                chat  = Chat(OPPONENTID,CURRENTID, ArrayList())
                                currChatReference.setValue(chat)
                                readChild()
                            }else{

                                chat =  Chat(CURRENTID, OPPONENTID, ArrayList())
                                currChatReference.setValue(chat)
                                readChild()
                            }

                        }

                    })




                }
            }

        })
        btnSend.setOnClickListener {
//            notify= true
            sendMessage()
        }
    }

    private fun initObject() {
        messages = ArrayList()
    }

    private fun readChild(){
        messagesReference = currChatReference.child("messages")
        messagesReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@ChatDetailActivity , "fail to fetch chat information", Toast.LENGTH_LONG).show()
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    messages= ArrayList()
                    for(p in dataSnapshot.children){
                        val message = p.getValue(Message::class.java)
                        messages!!.add(message!!)
                    }
                }
                getOpponentName()
            }

        })
    }

    private fun getOpponentName(){
        userReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val d: User? = p0.getValue(User::class.java)
                    Log.i("OPONNENTNAME", "awwww"+d!!.fullname)
                    opponentName = d.fullname

                    supportActionBar!!.setTitle(opponentName)
                    connectAdapter()
                }
            }
        })
        doctorReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val d: Doctor? = p0.getValue(Doctor::class.java)
                    Log.i("OPONNENTNAME", "awwww"+d!!.fullname)
                    opponentName = d.fullname
                    supportActionBar!!.setTitle(opponentName)
                    connectAdapter()
                }
            }
        })
    }
    @Synchronized private fun connectAdapter(){
        chatDetailAdapter = ChatDetailAdapter(
            messages,
            CURRENTID,
            opponentName
        )
        var l: LinearLayoutManager = LinearLayoutManager(this@ChatDetailActivity)
        l.stackFromEnd = true
        rvMessages.apply {
            setHasFixedSize(true)
            layoutManager = l

            adapter = chatDetailAdapter

        }

    }


    private fun initReferences() {
        userReference = FirebaseDatabase.getInstance().getReference("users").child(OPPONENTID)
        doctorReference = FirebaseDatabase.getInstance().getReference("doctors").child(OPPONENTID)
        chatReference = FirebaseDatabase.getInstance().getReference("chats")
    }

    private fun initToolbar() {

//        setSupportActionBar(toolBar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//       actionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initComponents(){
        btnSend = findViewById(R.id.btn_send)
        etMessage = findViewById(R.id.et_message)
        rvMessages = findViewById(R.id.rv_messages)

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendMessage() {

        if(etMessage.text.trim() == "" || etMessage.text == null){
            return
        }else{
            val c   = Calendar.getInstance()
            val text = etMessage.text
            var message:Message = Message(CURRENTID, OPPONENTID, text.toString(), c.time)
            val id: String? = messagesReference.push().key

            etMessage.text= null

            messagesReference.child(id!!).setValue(message)

            /*
            * FOR NOTTF
            * */
//            userReference.addValueEventListener(object: ValueEventListener {
//                override fun onCancelled(p0: DatabaseError) {
//
//                }
//
//                override fun onDataChange(p0: DataSnapshot) {
//                    val user = p0.getValue(User::class.java)
//                    if (notify) {
//                        sendNotification(OPPONENTID, user!!.fullname, text.toString())
//                    }
//                    notify = false
//
//                }
//
//            })

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu!!.clear()
        return super.onCreateOptionsMenu(menu)
    }

    private fun sendNotification(receiver:String, username:String, text:String){
        val tokens = FirebaseDatabase.getInstance().getReference("tokens")
        val query  = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(ds in p0.children){
                    val token: Token? = ds.getValue(Token::class.java)
                    val data = Data(CURRENTID, username, text, "New Message")

                    val sender = Sender(data, token!!.token!!)

                    apiService!!.sendNotification(sender)!!
                        .enqueue(object : Callback<MyResponse?> {
                            override fun onResponse(
                                call: Call<MyResponse?>,
                                response: Response<MyResponse?>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()!!.success != 1){
                                        Log.i("FAIL, NOTIF", "FAIL TO NOTIFY")
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<MyResponse?>,
                                t: Throwable
                            ) {

                            }
                        })
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
