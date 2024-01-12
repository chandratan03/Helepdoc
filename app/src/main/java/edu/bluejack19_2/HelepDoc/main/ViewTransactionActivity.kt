package edu.bluejack19_2.HelepDoc.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.TransactionAdapter
import edu.bluejack19_2.HelepDoc.auth.LoginActivity
import edu.bluejack19_2.HelepDoc.models.Transaction
import kotlinx.android.synthetic.main.activity_view_transaction.*

class ViewTransactionActivity : AppCompatActivity() {

    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private var transactions:ArrayList<Transaction> = ArrayList()
    val doctorRef =  FirebaseDatabase.getInstance().getReference("transaction")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_transaction)

        val sp = getSharedPreferences("Auth", Context.MODE_PRIVATE)
        if(sp.getString("id", "").equals("")) {
            Toast.makeText(applicationContext, "You are not authorized!", Toast.LENGTH_LONG).show()
            var intent = Intent(this@ViewTransactionActivity, LoginActivity::class.java)
            finish()
            startActivity(intent)
        }

        rvTransactions = findViewById(R.id.rv_transaction_view)
        transactionAdapter =
            TransactionAdapter(
                transactions,
                this
            )

        goback.setOnClickListener {
            goToProfile()
        }

        doctorRef.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(dataSnapShot: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                updateView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val trans = p0.getValue(Transaction::class.java)
                if((trans as Transaction).userId.equals(sp.getString("id", ""))) {
                    transactions.set(getIndex(trans as Transaction), trans)
                    updateView()
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val trans = p0.getValue(Transaction::class.java)
                if((trans as Transaction).userId.equals(sp.getString("id", ""))) {
                    transactions.add(trans!!)
                    updateView()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val trans = p0.getValue(Transaction::class.java)
                if((trans as Transaction).userId.equals(sp.getString("id", ""))) {
                    transactions.removeAt(getIndex(trans as Transaction))
                    updateView()
                }
            }

        })
    }

    private fun goToProfile() {
        var intent = Intent(this@ViewTransactionActivity, ProfileActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun getIndex(trans: Transaction): Int {
        var i = 0
        for(t in transactions) {
            if(trans.id.equals(t.id)) return i
            i++
        }
        return 0
    }

    private fun updateView() {
        rvTransactions.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ViewTransactionActivity, LinearLayoutManager.VERTICAL, false)
            adapter = transactionAdapter
        }
    }
}
