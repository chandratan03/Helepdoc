package edu.bluejack19_2.HelepDoc.mainFragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.DoctorRecommendAdapter
import edu.bluejack19_2.HelepDoc.adapters.ProductRecommendAdapter
import edu.bluejack19_2.HelepDoc.adapters.TransactionDetailAdapter
import edu.bluejack19_2.HelepDoc.models.Doctor
import edu.bluejack19_2.HelepDoc.models.Product
import edu.bluejack19_2.HelepDoc.models.Transaction
import java.lang.Exception
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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

    private lateinit var rvDoctor: RecyclerView
    private lateinit var rvProduct: RecyclerView
    private lateinit var rvLatest: RecyclerView
    private lateinit var doctorAdapter: DoctorRecommendAdapter
    private lateinit var productAdapter: ProductRecommendAdapter
    private lateinit var latestAdapter: TransactionDetailAdapter
    private var doctors: Vector<Doctor> = Vector()
    private var recDoctors: Vector<Doctor> = Vector()
    private var products: Vector<Product> = Vector()
    private var recProducts: Vector<Product> = Vector()
    private var transactions: Vector<Transaction> = Vector()
    private var latestTran: Transaction = Transaction()
    val docRef =  FirebaseDatabase.getInstance().getReference("doctors")
    val proRef =  FirebaseDatabase.getInstance().getReference("products")
    val tranRef =  FirebaseDatabase.getInstance().getReference("transaction")

    lateinit var act: Activity

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        act = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sp = act.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val loading = view.findViewById<RelativeLayout>(R.id.loading)
        loading.bringToFront()
        rvDoctor = view.findViewById(R.id.rv_doctor_rec)
        rvProduct = view.findViewById(R.id.rv_product_rec)
        rvLatest = view.findViewById(R.id.rv_latest)



        docRef.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(dataSnapShot: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                recDoctors.clear()
                initDoctor()
                updateDoctorView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val doc = p0.getValue(Doctor::class.java)
                doctors.set(getIndex(doc!!), doc)
                recDoctors.clear()
                initDoctor()
                updateDoctorView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val doc = p0.getValue(Doctor::class.java)
                doctors.add(doc)
                recDoctors.clear()
                initDoctor()
                updateDoctorView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val doc = p0.getValue(Doctor::class.java)
                doctors.removeAt(getIndex(doc!!))
                recDoctors.clear()
                initDoctor()
                updateDoctorView()
            }

        })

        proRef.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                recProducts.clear()
                initProduct()
                updateProductView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val pro = p0.getValue(Product::class.java)
                products.set(getIndex(pro as Product), pro)
                recProducts.clear()
                initProduct()
                updateProductView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val pro = p0.getValue(Product::class.java)
                products.add(pro)
                recProducts.clear()
                initProduct()
                updateProductView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val pro = p0.getValue(Product::class.java)
                products.removeAt(getIndex(pro as Product))
                recProducts.clear()
                initProduct()
                updateProductView()
            }
        })

        tranRef.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                val tr = p0.getValue(Transaction::class.java)
                if(sp.getString("id","").equals(tr!!.userId)) {
                    getLatestTran(view)
                    updateLatestView(view)
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val tr = p0.getValue(Transaction::class.java)
                if(sp.getString("id","").equals(tr!!.userId)) {
                    transactions.set(getIndex(tr), tr)
                    getLatestTran(view)
                    updateLatestView(view)
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val tr = p0.getValue(Transaction::class.java)
                if(sp.getString("id","").equals(tr!!.userId)) {
                    transactions.add(tr)
                    getLatestTran(view)
                    updateLatestView(view)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val tr = p0.getValue(Transaction::class.java)
                if(sp.getString("id","").equals(tr!!.userId)) {
                    transactions.removeAt(getIndex(tr))
                    getLatestTran(view)
                    updateLatestView(view)
                }
            }
        })
        getLatestTran(view)

        Handler().postDelayed(Runnable {
            loading.visibility = View.GONE
        }, 2000)

        return view
    }

    private fun updateDoctorView() {
        try {
            doctorAdapter =
                DoctorRecommendAdapter(
                    recDoctors,
                    this.context!!
                )
            rvDoctor.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this.context!!, LinearLayoutManager.HORIZONTAL, false)
                adapter = doctorAdapter
            }
        }
        catch (e: Exception) {

        }
    }

    private fun updateProductView() {
        productAdapter =
            ProductRecommendAdapter(
                recProducts,
                this.context!!
            )
        rvProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context!!, LinearLayoutManager.HORIZONTAL, false)
            adapter = productAdapter
        }
    }

    private fun updateLatestView(view: View) {
        view.findViewById<TextView>(R.id.tv_transaction_date).setText("Transaction Date: " + latestTran.transactionDate)
        view.findViewById<TextView>(R.id.tv_grand_total).setText("Grand Total: IDR " + latestTran.userBalance)
        latestAdapter =
            TransactionDetailAdapter(
                latestTran.carts!!
            )
        rvLatest.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context!!, LinearLayoutManager.HORIZONTAL, false)
            adapter = latestAdapter
        }
    }


    private fun initDoctor() {
        Collections.sort(doctors, DoctorComparator())
        var i = 0
        for(d in doctors) {
            if(i == 5) break
            recDoctors.add(d)
            i++
        }
    }

    private fun initProduct() {
        Collections.sort(products, ProductComparator())
        var i = 0
        for(r in products) {
            if(i == 5) break
            recProducts.add(r)
            i++
        }
    }

    private fun getLatestTran(view: View) {
        if(transactions.size != 0) {
            view.findViewById<TextView>(R.id.tv_latest_purchase).setText("Your Latest Transaction")
            Collections.sort(transactions, LatestDateComparator())
            latestTran = transactions[0]
            view.findViewById<LinearLayout>(R.id.latest_layout).visibility = View.VISIBLE
        }
        else {
            view.findViewById<TextView>(R.id.tv_latest_purchase).setText("No Transaction")
            view.findViewById<LinearLayout>(R.id.latest_layout).visibility = View.GONE
        }
    }

    class DoctorComparator : Comparator<Doctor?> {
        override fun compare(p0: Doctor?, p1: Doctor?): Int {
            return (p1!!.rating/(p1).count_rate).compareTo(p0!!.rating/(p0).count_rate)
        }
    }

    class ProductComparator : Comparator<Product?> {
        override fun compare(p0: Product?, p1: Product?): Int {
            return (p1!!.rating/(p1).count_rate).compareTo(p0!!.rating/(p0).count_rate)
        }
    }

    class LatestDateComparator : Comparator<Transaction?> {
        override fun compare(p0: Transaction?, p1: Transaction?): Int {
            return (p1!!.transactionDate)!!.compareTo(p0!!.transactionDate)
        }

    }

    private fun getIndex(doc: Doctor): Int {
        var i = 0
        for(d in doctors) {
            if(d.id.equals(doc.id)) return i
            i++
        }
        return 0
    }

    private fun getIndex(pro: Product): Int {
        var i = 0
        for(p in products) {
            if(p.id.equals(pro.id)) return i
            i++
        }
        return 0
    }

    private fun getIndex(tr: Transaction): Int {
        var i = 0
        for(t in transactions) {
            if(t.id.equals(tr.id)) return i
            i++
        }
        return 0
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
