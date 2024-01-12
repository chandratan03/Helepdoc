package edu.bluejack19_2.HelepDoc

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.bluejack19_2.HelepDoc.ProductPage.Companion.carts
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Cart
import edu.bluejack19_2.HelepDoc.models.Product
import java.util.*

class ProductDetail : AppCompatActivity() {

    private lateinit var ivProductImage:ImageView
    private lateinit var tvProductName:TextView
    private lateinit var tvProductPrice:TextView
    private lateinit var tvIndicator:TextView
    private lateinit var tvAttention:TextView
    private lateinit var tvDosage:TextView
    private lateinit var tvComposition:TextView
    private lateinit var tvQuantity:TextView
    private lateinit var tvProductRating: TextView
    private lateinit var btnAdd:Button
    private lateinit var btnDelete:Button
    private lateinit var btnAddToCart:Button
    private lateinit var storageReference:StorageReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var product: Product
    private lateinit var plusMinLayout: LinearLayout
    private var cart:Cart? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        initComponents()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Product"

        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        storageReference = FirebaseStorage.getInstance().getReference("products")

        val id:String? =intent.getStringExtra("productId")
        getProductById(id!!)

    }
    override fun onBackPressed() {
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
    fun initCart(){
        var index:Int = -1
        for((i, cart) in carts.withIndex()){
            if(cart.product.id.equals(product.id)){
                index = i
            }
        }
        if(index!=-1){
            cart = carts[index]
            tvQuantity.text = cart!!.quantity.toString()
        }else{
            tvQuantity.text = "0"
            cart = null
        }
    }

    fun checkButton(){
        if(cart == null){
            btnAddToCart.visibility = View.VISIBLE
            plusMinLayout.visibility = View.GONE
        }else{
            btnAddToCart.visibility = View.GONE
            plusMinLayout.visibility = View.VISIBLE

        }
    }

    fun initButtons(){
        btnAddToCart.setOnClickListener {
            cart = Cart(product, 1)
            carts.add(cart!!)
            tvQuantity.text = cart!!.quantity.toString()
            checkButton()
        }
        btnAdd.setOnClickListener(View.OnClickListener {
            if(cart == null){
                cart = Cart(product, 1)
                carts.add(cart!!)
                tvQuantity.text = cart!!.quantity.toString()
            }else{
                cart!!.quantity += 1
                tvQuantity.text = cart!!.quantity.toString()
            }
        })
        btnDelete.setOnClickListener(View.OnClickListener {
            if(cart!=null){
                if(cart!!.quantity == 1){
                    carts.remove(cart!!)
                    cart = null
                    tvQuantity.text = "0"
                }else{
                    cart!!.quantity--
                    tvQuantity.text = cart!!.quantity.toString()
                }
            }
            checkButton()
        })
        checkButton()
    }



    private fun getProductById(id:String){
        val ref:DatabaseReference =databaseReference.child(id)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@ProductDetail, "fail to load product", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
//                    for(p in p0.children){
//                        product = p.getValue(Product::class.java)!!
//                        Log.e("hehe",p.getValue().toString())
//                    }
                    product = p0.getValue(Product::class.java)!!
                    tvProductName.text = product.name
                    tvProductPrice.text = "IDR "+ product.price.toInt().toString()
                    tvIndicator.text = product.indicator
                    tvAttention.text = product.attention
                    tvDosage.text = product.dosage
                    tvComposition.text = product.composition
                    tvProductRating.text ="Rate :"+product.rating
                    Glide.with(this@ProductDetail).load(product.image).into(ivProductImage)
                    initCart()
                    initButtons()
                }else{
                    Log.e("NOT", "FOUND")
                }

            }

        })

    }

    private fun initComponents(){
        ivProductImage = findViewById(R.id.iv_product_image)
        tvProductName = findViewById(R.id.tv_product_name)
        tvProductPrice = findViewById(R.id.tv_product_price)
        tvIndicator = findViewById(R.id.tv_indicator)
        tvAttention = findViewById(R.id.tv_attention)
        tvDosage = findViewById(R.id.tv_dosage)
        tvComposition = findViewById(R.id.tv_composition)
        btnAdd = findViewById(R.id.btn_add)
        tvQuantity = findViewById(R.id.tv_quantity)
        btnDelete = findViewById(R.id.btn_delete)
        btnAddToCart = findViewById(R.id.btn_add_to_cart)
        plusMinLayout = findViewById(R.id.plus_min)
        tvProductRating = findViewById(R.id.tv_product_rating)
    }

}
