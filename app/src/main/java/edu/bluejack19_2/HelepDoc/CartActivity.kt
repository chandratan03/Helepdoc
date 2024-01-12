package edu.bluejack19_2.HelepDoc

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack19_2.HelepDoc.ProductPage.Companion.carts
import edu.bluejack19_2.HelepDoc.adapters.CartAdapter
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Transaction


class CartActivity : AppCompatActivity() {
    private lateinit var rvCart:RecyclerView
    private lateinit var cartAdapter: CartAdapter
    lateinit var btnCheckout:Button
    private var USERID:String? = "-M43c_mp8Ur1bDV3PksP"
    private lateinit var databaseReference: DatabaseReference
    private lateinit var tvMsg: RelativeLayout
    companion object{
        var totalPayment: Double = 0.0
    }

    override fun onBackPressed() {
//        val intent:Intent = Intent(this@CartActivity, NavigatorActivity::class.java)
//        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        USERID = sp.getString("id", "")



        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cart"

        databaseReference = FirebaseDatabase.getInstance().getReference("transaction")

        rvCart = findViewById(R.id.rv_carts)
        cartAdapter = CartAdapter(
            carts,
            this@CartActivity
        )
        btnCheckout = findViewById(R.id.btn_checkout)
        tvMsg  = findViewById(R.id.tv_msg)
        updateTotalPayment()
        rvCart.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@CartActivity)
            this.adapter = cartAdapter
        }

        var dialogListener:DialogInterface.OnClickListener = DialogInterface.OnClickListener{
                _, which ->
            if(which == DialogInterface.BUTTON_POSITIVE){
                val id:String? = databaseReference.push().key
                val  calendar:Calendar = Calendar.getInstance()
                val transaction:Transaction = Transaction(id, carts, USERID!!, calendar.time,
                    totalPayment
                )
                databaseReference.child(id!!).setValue(transaction)
                carts.removeAll(carts)
                finish()
            }else if(which==DialogInterface.BUTTON_NEGATIVE){
                //Do nothing
            }


        }

        btnCheckout.setOnClickListener(View.OnClickListener {
            updateTotalPayment()
            var builder : AlertDialog.Builder = AlertDialog.Builder(this@CartActivity)
            builder.setMessage("Total payment: $totalPayment").setPositiveButton("Yes",dialogListener)
                .setNegativeButton("No",dialogListener).show()

        })

        checkButton()

    }

    fun checkButton(){
        if(carts.isEmpty()){
            tvMsg.visibility = View.VISIBLE
            btnCheckout.visibility =View.GONE
        }else{
            btnCheckout.visibility= View.VISIBLE
            tvMsg.visibility = View.GONE
        }
    }
    fun updateTotalPayment(){
        if(!carts.isEmpty()){
            totalPayment = 0.0;
            for(i in carts){
                totalPayment += i.quantity * i.product.price
            }

        }else{
            totalPayment = 0.0
        }
        btnCheckout.text= "CHECKOUT $totalPayment"
    }

}
