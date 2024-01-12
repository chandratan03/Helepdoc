package edu.bluejack19_2.HelepDoc.mainFragments

import android.annotation.SuppressLint
import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Color
import android.os.Bundle
import android.provider.BaseColumns
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.ProductAdapter
import edu.bluejack19_2.HelepDoc.models.Cart
import edu.bluejack19_2.HelepDoc.models.Product

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductFragment : Fragment() {
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


    private lateinit var rvProduct: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var products:ArrayList<Product>
    private lateinit var PRODUCTS:ArrayList<Product>
    private lateinit var productNames:ArrayList<String>
    private val drugDatabaseRef=  FirebaseDatabase.getInstance().getReference("products")
    private val drugStorageReference = FirebaseStorage.getInstance().getReference("products")

    private lateinit var svProduct: SearchView

    private lateinit var acTextView: AutoCompleteTextView
    private lateinit var acAdapter:ArrayAdapter<String>
    private lateinit var myView:View

    private lateinit var loading:RelativeLayout


    private lateinit var cursorAdapter: SimpleCursorAdapter


//    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View =  inflater.inflate(R.layout.fragment_product, container, false)
//        progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        progressDialog.show()

        productNames = ArrayList()

        myView = view

    //LOADING
        loading = view.findViewById(R.id.loading)

        rvProduct  =  view.findViewById(R.id.rv_product)
        products = ArrayList()
        PRODUCTS = ArrayList()





        drugDatabaseRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(dataSnapShot: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (p in dataSnapshot.children) {
                        // MAKE SURE THE DRUG Constructor is Initialize -> check on the models
                        val product = p.getValue(Product::class.java)
                        products.add(product!!)
                    }
                    PRODUCTS.addAll(products)
                    if(productNames.isEmpty())
                        for (p in PRODUCTS) {
                            productNames.add(p.name)
                        }
                    productAdapter =
                        ProductAdapter(
                            products,
                            view.context!!
                        )
                    rvProduct.apply {
                        setHasFixedSize(true)
                        layoutManager = GridLayoutManager(view.context, 2)
                        adapter = productAdapter
                    }
                    var loading:RelativeLayout =view.findViewById(R.id.loading)
                    loading.visibility = View.GONE
                    setHasOptionsMenu(true)
                }
            }
        })



//        svProduct.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                updateDatas(query)
//
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                updateDatas(newText)
//                return false
//            }
//
//        })




        return view

    }

    @SuppressLint("ResourceType")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchItem = menu.findItem(R.id.sv_product)
        svProduct = searchItem?.actionView as SearchView
        svProduct.queryHint = "Search product ..."
//        val tv = svProduct.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
//        tv.threshold = 1


        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
//        val layout:TextView = myView.findViewById(android.R.layout.simple_dropdown_item_1line)
//        layout.setBackgroundColor(Color.parseColor("#FFFFFF"))
        val to = intArrayOf(android.R.id.text1)
        cursorAdapter = SimpleCursorAdapter(context,
            R.layout.my_search_layout,
            null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

        svProduct.suggestionsAdapter =cursorAdapter
        svProduct.setOnSuggestionListener(object: SearchView.OnSuggestionListener{
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor  = svProduct.suggestionsAdapter.getItem(position)  as Cursor
                val text = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                svProduct.setQuery(text, true)

                return true

            }


        })


        svProduct.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateDatas(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val cursor  = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                newText?.let{
                    productNames.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(newText, true))
                            cursor.addRow(arrayOf(index, suggestion.toString()))
                    }
                }
                cursorAdapter.changeCursor(cursor)
                updateDatas(newText)
                return true
            }

        })


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.product_menu,menu)


        super.onCreateOptionsMenu(menu, inflater)
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var carts :ArrayList<Cart>  = ArrayList()
    }















//    private fun insertProduct(p: Product, image: Int, ){
//        val storageRef = drugStorageReference.child(p.id.toString())
//        val bitmap:Bitmap = BitmapFactory.decodeResource(resources, image)
//        var fos: FileOutputStream?
//        val file2: File = view.applicationContext.filesDir
//        val imageFile = File(file2, "test.jpg") // just let the child parameter empty
//
//        fos = FileOutputStream(imageFile)
//
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
//
//        fos.close()
//        val uri:Uri = Uri.fromFile(imageFile)
//        storageRef.putFile(uri).addOnSuccessListener {
//            storageRef.downloadUrl.addOnCompleteListener {
//                    taskSnapshot ->
//                val imageSrc:String= taskSnapshot.result.toString()
//                p.image = imageSrc
//                products.add(p)
//                drugDatabaseRef.child(p.id!!).setValue(p)
//            }
//
//        }
//    }
//
//
//
//    fun initProducts(){
//        var id:String? = drugDatabaseRef.push().key    // generateID
////
//        id = drugDatabaseRef.push().key
//
//        var p: Product = Product(id,
//            "Per tablet" ,
//            "FLUCONAZOLE 150 MG KAPSUL",
//            "INFORMASI OBAT INI HANYA UNTUK KALANGAN MEDIS. Menganitis kriptokokal, Kandidiasis sistemik, kandidiasis orofaringeal, kandidiasis vagina akul atau relaps, infeksi kandida superfisial, infeksi kandida, iskemik atau infeksi kriptokokal" ,
//            "HARUS DENGAN RESEP DOKTER. AIDS. Hamil & laktasi. Anak < 18 tahun Kategori kehamilan: C, D (pada trimester 2 dan 3)\n" ,
//            "PENGGUNAAN OBAT INI HARUS SESUAI DENGAN PETUNJUK DOKTER.\n" +
//                    "Dewasa menginitis kriptokokal : hari ke-1 : 400 mg sebagai dosis tunggal; hari ke-2 dan seterusnya 200 - 400 mg per hari. Lama terapi : 6 - 8 minggu. \n" +
//                    "Kandidiasis mukosal: 50 mg/hari selama 14 hari.\n" +
//                    "Kandidiasis vagian: 150 mg sebagai dosis tunggal oral." ,
//            "Fluconazole 150 mg" ,
//            20700.00 ,
//            "",
//            4.0)
//        insertProduct(p, R.drawable.fluconazole)
//
////
////        id = drugDatabaseRef.push().key
////        var p2 = Product(
////            id,
////            "Per strip",
////            "KETOCONAZOLE 200 MG 10 TABLET",
////            "INFORMASI OBAT INI HANYA UNTUK KALANGAN MEDIS. Infeksi jamur sistemik, kandidiasis mukokutan kronis yang tidak responsif terhadap nistatin & obat-obat lainnya\n",
////            "HARUS DENGAN RESEP DOKTER. Wanita hamil dan menyusui. Penderita dengan gangguan fungsi hati dan Insufisiensi adrenal. Kategori Kehamilan: C",
////            "PENGGUNAAN OBAT INI HARUS SESUAI DENGAN PETUNJUK DOKTER. Infeksi mikosis: Dewasa 1 tablet per hari selama 14 hari. Jika respon tidak ada, dapat ditingkatkan menjadi 400 mg. Kandidiasis vaginal: 2 tablet selama 5 hari.",
////            "Ketoconazole 200 mg",
////            5200.00,
////            "",
////            5.0
////        )
////        insertProduct(p2, R.drawable.ketoconazole)
////
//        id=drugDatabaseRef.push().key
//        var p3 = Product(
//            id,
//            "Per strip",
//            "MECOBALAMIN 500 MCG 10 KAPSUL",
//            "INFORMASI OBAT INI HANYA UNTUK KALANGAN MEDIS. Neuropati perifer, tinitus, vertigo, anemia megalobastik karena defisiensi vitamin B12\n",
//            "HARUS DENGAN RESEP DOKTER. Hipersensitif komponen",
//            "PENGGUNAAN OBAT INI HARUS SESUAI DENGAN PETUNJUK DOKTER. 3 x sehari 1 kapsul\n",
//            "Mecobalamin 500 mg\n",
//            8800.00f,
//            "",
//            4.5
//        )
//        insertProduct(p3, R.drawable.mecobalamin)
//    }

}
