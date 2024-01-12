package edu.bluejack19_2.HelepDoc.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*
import kotlin.Comparator


class HomeActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sp = getSharedPreferences("Auth", Context.MODE_PRIVATE)
        
        rvDoctor = findViewById(R.id.rv_doctor_rec)
        rvProduct = findViewById(R.id.rv_product_rec)
        rvLatest = findViewById(R.id.rv_latest)

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
                    getLatestTran()
                    updateLatestView()
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val tr = p0.getValue(Transaction::class.java)
                if(sp.getString("id","").equals(tr!!.userId)) {
                    transactions.set(getIndex(tr), tr)
                    getLatestTran()
                    updateLatestView()
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val tr = p0.getValue(Transaction::class.java)
                if(sp.getString("id","").equals(tr!!.userId)) {
                    transactions.add(tr)
                    getLatestTran()
                    updateLatestView()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val tr = p0.getValue(Transaction::class.java)
                if(sp.getString("id","").equals(tr!!.userId)) {
                    transactions.removeAt(getIndex(tr))
                    getLatestTran()
                    updateLatestView()
                }
            }
        })
        getLatestTran()

    }


    private fun updateDoctorView() {
        doctorAdapter =
            DoctorRecommendAdapter(
                recDoctors,
                this
            )
        rvDoctor.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = doctorAdapter
        }
    }

    private fun updateProductView() {
        productAdapter =
           ProductRecommendAdapter(
                recProducts,
                this
            )
        rvProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = productAdapter
        }
    }

    private fun updateLatestView() {
        findViewById<TextView>(R.id.tv_transaction_date).setText("Transaction Date: " + latestTran.transactionDate)
        findViewById<TextView>(R.id.tv_grand_total).setText("Grand Total: IDR " + latestTran.userBalance)
        latestAdapter =
            TransactionDetailAdapter(
                latestTran.carts!!
            )
        rvLatest.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
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

    private fun getLatestTran() {
        if(transactions.size != 0) {
            findViewById<TextView>(R.id.tv_latest_purchase).setText("Your Latest Transaction")
            Collections.sort(transactions, LatestDateComparator())
            latestTran = transactions[0]
            findViewById<LinearLayout>(R.id.latest_layout).visibility = View.VISIBLE
        }
        else {
            findViewById<TextView>(R.id.tv_latest_purchase).setText("No Transaction")
            findViewById<LinearLayout>(R.id.latest_layout).visibility = View.GONE
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
}
