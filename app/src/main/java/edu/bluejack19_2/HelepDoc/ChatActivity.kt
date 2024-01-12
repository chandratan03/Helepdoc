package edu.bluejack19_2.HelepDoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import edu.bluejack19_2.HelepDoc.adapters.ChatAdapter
import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.chatFragments.ChatFragment
import edu.bluejack19_2.HelepDoc.chatFragments.DoctorFragment


class ChatActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var titles: ArrayList<String>

    private lateinit var chatAdapter: ChatAdapter

    private val DOCTORID:String = "-M48nlk7aItLyYcj_b5G"
    private val USERID:String = "-M43c_mp8Ur1bDV3PksP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initVars()
        connectAdapter()


    }


    private fun initVars() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        fragmentList = ArrayList()
        titles = ArrayList()
        fragmentList.add(DoctorFragment())
        titles.add("Doctors")
        fragmentList.add(ChatFragment())
        titles.add("Chats")
    }

    private fun connectAdapter() {
        chatAdapter = ChatAdapter(
            supportFragmentManager,
            fragmentList,
            titles
        )
        viewPager.adapter = chatAdapter
        tabLayout.setupWithViewPager(viewPager)
    }
}
