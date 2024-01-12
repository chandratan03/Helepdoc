package edu.bluejack19_2.HelepDoc

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack19_2.HelepDoc.adapters.ProductAdapter
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Cart
import edu.bluejack19_2.HelepDoc.models.Product
import java.io.File
import java.io.FileOutputStream

class ProductPage : AppCompatActivity() {
    private lateinit var rvProduct:RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var products:ArrayList<Product>
    private lateinit var PRODUCTS:ArrayList<Product>
    private val drugDatabaseRef=  FirebaseDatabase.getInstance().getReference("products")
    private val drugStorageReference = FirebaseStorage.getInstance().getReference("products")

    private lateinit var svProduct:SearchView

    companion object{
        var carts :ArrayList<Cart>  = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_page)
        svProduct = findViewById(R.id.sv_product)
        rvProduct  =  findViewById(R.id.rv_product)
        products = ArrayList()
        PRODUCTS = ArrayList()
        // WRITE
        // !! -> means data non null/ promise that never null

        // READ DATA
        drugDatabaseRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(dataSnapShot: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                 if(dataSnapshot.exists()){
                     for(p in dataSnapshot.children){
                         // MAKE SURE THE DRUG Constructor is Initialize -> check on the models
                         val product = p.getValue(Product::class.java)
                         products.add(product!!)
                     }
                     PRODUCTS.addAll (products)
                     productAdapter =
                         ProductAdapter(
                             products,
                             this@ProductPage
                         )
                        rvProduct.apply {
                            setHasFixedSize(true)
                            layoutManager = GridLayoutManager(this@ProductPage, 2)
                            adapter = productAdapter
                        }
                    }
            }
        })
        svProduct.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateDatas(query)

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateDatas(newText)
                return false
            }

        })

    }

    fun showCart(menuItem:MenuItem){
        if(menuItem.itemId == R.id.btn_cart){
            val intent:Intent = Intent(this@ProductPage, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun insertProduct(p: Product, image: Int){
        val storageRef = drugStorageReference.child(p.id.toString())
        val bitmap:Bitmap = BitmapFactory.decodeResource(resources, image)
        var fos: FileOutputStream?
        val file2:File= applicationContext.filesDir
        val imageFile = File(file2, "test.jpg") // just let the child parameter empty

        fos = FileOutputStream(imageFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)

        fos.close()
        val uri:Uri = Uri.fromFile(imageFile)
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnCompleteListener {
                taskSnapshot ->
                    val imageSrc:String= taskSnapshot.result.toString()
                    p.image = imageSrc
                    products.add(p)
                    drugDatabaseRef.child(p.id!!).setValue(p)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_menu, menu)
        return true
    }

    fun updateDatas(query:String?){
        products.removeAll(products)
        if(query == null){
            products.addAll(PRODUCTS)
        }else{

            for(p in PRODUCTS){
                if(p.name.toUpperCase().contains(query!!.toUpperCase())){
                    products.add(p)
                }
            }
        }
        productAdapter.notifyDataSetChanged()

    }

}