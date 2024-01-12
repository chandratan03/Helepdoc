package edu.bluejack19_2.HelepDoc

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView

import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.bluejack19_2.HelepDoc.auth.LoginActivity
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.models.Product

import java.io.File
import java.io.FileOutputStream


// THIS MAIN ACTIVITY ONLY FOR SEEDING PURPOSE
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        drawerLayout = findViewById(R.id.drawer_layout)
//        navigationView = findViewById(R.id.nav_view)
//        navigationView.setNavigationItemSelectedListener(this)
//
////        actionBar!!.setDisplayHomeAsUpEnabled(true);
////        actionBar!!.setHomeButtonEnabled(true);
//
//        val intent = Intent(this@MainActivity, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
        initProducts()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.item_chat ->{

                val intent = Intent(this@MainActivity,ChatActivity::class.java)
                startActivity(intent)
            }
            R.id.item_product ->{
                val intent = Intent(this@MainActivity,ProductPage::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        p0.isChecked = false
//        finish()
        Log.e("test", "test")

        return false
    }


    private fun test(){




//        val storageRef:StorageReference = FirebaseStorage.getInstance().getReference("images2").child("hehe2")
//
//        val bitmap:Bitmap = BitmapFactory.decodeResource(resources, R.drawable.test)
//
//        var fos: FileOutputStream?
//        val file2:File= applicationContext.filesDir
//        val imageFile = File(file2, "test.jpg") // just let the child parameter empty
//
//        fos = FileOutputStream(imageFile)
//
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
//
//        fos.close()



//        val file:File = File("", "")
//        if(imageFile.exists()){
//            Toast.makeText(this, "gotcha", Toast.LENGTH_LONG).show()
//        }else{
//            Toast.makeText(this, "Nope", Toast.LENGTH_LONG).show()
//
//        }
//        val uri:Uri = Uri.fromFile(imageFile)
//        storageRef.putFile(uri).addOnSuccessListener {
//            taskSnapshot ->
//            storageRef.downloadUrl.addOnCompleteListener {
//                taskSnapshot ->
//                // get image uri FOR PATH OF IMAGE
//                    Log.i("IMAGEHERE:", taskSnapshot.result.toString())
//            }
//
//        }



        ///READ IMAGEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//        var iv:ImageView= findViewById(R.id.testImage)
//        storageRef.downloadUrl.addOnSuccessListener {
//            uri ->
//            Glide.with(this).load(uri).into(iv)
//            Log.i("IMAGEHERE:", uri.toString())
//        }



    }


    private val drugStorageReference = FirebaseStorage.getInstance().getReference("products")
    private val drugDatabaseRef=  FirebaseDatabase.getInstance().getReference("products")

    private fun insertProduct(p: Product, image: Int){
        val storageRef = drugStorageReference.child(p.id.toString())
        val bitmap:Bitmap = BitmapFactory.decodeResource(resources, image)
        var fos: FileOutputStream?
        val file2: File = applicationContext.filesDir
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
                drugDatabaseRef.child(p.id!!).setValue(p)
            }

        }
    }
//
//
//
    fun initProducts(){
        var id:String? = drugDatabaseRef.push().key    // generateID
//
        id = drugDatabaseRef.push().key

        var p: Product = Product(
            id,
            "Per tablet" ,
            "FLUCONOLOLALE",
            "INFORMASI OBAT INI HANYA UNTUK KALANGAN MEDIS. INI TIDAK COCOK UNTUK ANAK YANG MEMILIKI ASTHMA, DAN ORANG MEMILIKI DIABETES" ,
            "HARUS DENGAN RESEP DOKTER. ORANG YANG HAMIL DAN PERMASALAHAN DENGAN PERNAPASAN DILARANG KONSUMSI\n" ,
            "PENGGUNAAN OBAT INI HARUS SESUAI DENGAN PETUNJUK DOKTER.\n" +
                    "Dewasa: 1 hari 3x\n" +
                    "Anaka: 1 hari 1x\n",
            "Fluconololale 150mg" ,
            310000.00f ,
            "",
            0)
        insertProduct(p, R.drawable.photo1)
//
////
//        id = drugDatabaseRef.push().key
//        var p2 = Product(
//            id,
//            "Per strip",
//            "KETOCON TABLET",
//            "INFORMASI OBAT INI HANYA UNTUK KALANGAN MEDIS. HARAP TIDAK ASAL DIGUNAKAN\n",
//            "HARUS DENGAN RESEP DOKTER. Orang yang memiliki hepatitis dilarang untuk mencoba produk ini",
//            "PENGGUNAAN OBAT INI HARUS SESUAI DENGAN PETUNJUK DOKTER. Dewasa 1 hari 5x, anak-anak 1 hari 2x",
//            "Ketocon 500 mg",
//            3000.00f,
//            "",
//            5
//        )
//        insertProduct(p2, R.drawable.photo2)
////
//        id=drugDatabaseRef.push().key
//        var p3 = Product(
//            id,
//            "Per strip",
//            "MORELOBA 20 KAPSUL",
//            "INFORMASI OBAT INI HANYA UNTUK KALANGAN MEDIS.\n",
//            "HARUS DENGAN RESEP DOKTER. Mengandung Sensitif komponen",
//            "PENGGUNAAN OBAT INI HARUS SESUAI DENGAN PETUNJUK DOKTER. Setiap umur 3 x sehari 1 kapsul, kecuali untuk anak < 5 tahun\n",
//            "Moreloba 500 mg\n",
//            10000.00f,
//            "",
//            4
//        )
//        insertProduct(p3, R.drawable.photo3)
    }
}