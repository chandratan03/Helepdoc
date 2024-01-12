package edu.bluejack19_2.HelepDoc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.internal.NavigationMenuItemView
import edu.bluejack19_2.HelepDoc.auth.LoginActivity

class NavigatorActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var svProduct: SearchView
    private lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigator)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        navView= findViewById(R.id.nav_view)
        val headerView= navView.getHeaderView(0)


        val tvTitle:TextView = headerView.findViewById(R.id.nav_header_title)
        val tvSubTitle:TextView = headerView.findViewById(R.id.nav_header_subtitle)

        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        val fullname = sp.getString("fullname", "")
        val email = sp.getString("email","")
        val role = sp.getString("role", "")
        tvTitle.text= fullname
        tvSubTitle.text=email
        

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_main_chat,
            R.id.nav_chat,
            R.id.nav_product,
            R.id.nav_profile,
            R.id.nav_transaction,
            R.id.nav_doctor
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        hideNavItem(role)
    }


    private fun hideNavItem(role:String?) {
        val menu = navView.menu
        // HIDE HERE
        if (role == "Doctor"){
            menu.findItem(R.id.nav_main_chat).isVisible = false
//            menu.findItem(R.id.nav_home).isVisible= false
            menu.findItem(R.id.nav_product).isVisible= false
            menu.findItem(R.id.nav_profile).isVisible= false
            menu.findItem(R.id.nav_transaction).isVisible= false
            menu.findItem(R.id.nav_doctor).isVisible = false


        }
        if(role== "User")
            menu.findItem(R.id.nav_chat).isVisible=false
    }
    private fun logout(): Boolean {
        var sp = getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val auth = sp.edit()
        auth.putString("id", "")
        auth.putString("fullname", "")
        auth.putString("email", "")
        auth.putString("password", "")
        auth.putString("phone", "")
        auth.putString("gender", "")
        auth.putString("dob", "")
        auth.putFloat("balance", 0.0f)
        auth.putString("picture", "")
        auth.putString("comeFrom", "")
        auth.commit()
        var intent = Intent(this@NavigatorActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        return false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigator, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    // USE BY PRODUCT FRAGMENT
    fun showCart(menuItem: MenuItem){
        if(menuItem.itemId == R.id.btn_cart){
            val intent: Intent = Intent(this@NavigatorActivity, CartActivity::class.java)
            startActivity(intent)
        }
    }
}
