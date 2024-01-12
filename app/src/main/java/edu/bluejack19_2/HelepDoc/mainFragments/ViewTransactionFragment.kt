package edu.bluejack19_2.HelepDoc.mainFragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewTransactionFragment : Fragment() {
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

    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private var transactions:ArrayList<Transaction> = ArrayList()
    val transactionRef =  FirebaseDatabase.getInstance().getReference("transaction")

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
        val view = inflater.inflate(R.layout.fragment_view_transaction, container, false)
        cont = this.context!!
        val sp = act.getSharedPreferences("Auth", Context.MODE_PRIVATE)

        val loading = view.findViewById<RelativeLayout>(R.id.loading)
        loading.bringToFront()
        rvTransactions = view.findViewById(R.id.rv_transaction_view)
        transactionAdapter =
            TransactionAdapter(
                transactions,
                cont
            )

        transactionRef.addChildEventListener(object: ChildEventListener {
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

        Handler().postDelayed(Runnable {
            loading.visibility = View.GONE
            if(transactions.size == 0) {
                rvTransactions.visibility = View.GONE
            }
            else {
                view.findViewById<TextView>(R.id.no_transaction).visibility = View.GONE
            }
        }, 2000)

        return view
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
            layoutManager = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
            adapter = transactionAdapter
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewTransactionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
