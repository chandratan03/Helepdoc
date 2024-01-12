package edu.bluejack19_2.HelepDoc.mainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import edu.bluejack19_2.HelepDoc.R
import edu.bluejack19_2.HelepDoc.adapters.ChatAdapter
import edu.bluejack19_2.HelepDoc.chatFragments.ChatFragment
import edu.bluejack19_2.HelepDoc.chatFragments.DoctorFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainChatFragment : Fragment() {
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

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var titles: ArrayList<String>
    private lateinit var chatAdapter: ChatAdapter


    private lateinit var view2:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_main_chat, container, false)
        view2 = view
        initVars(view)
        connectAdapter()
        return view
    }



    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun initVars(view:View) {
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        fragmentList = ArrayList()
        titles = ArrayList()
        fragmentList.add(DoctorFragment())
        titles.add("Doctors")
        fragmentList.add(ChatFragment())
        titles.add("Chats")
    }

    private fun connectAdapter() {
        chatAdapter = ChatAdapter(
            fragmentManager!!,
            fragmentList,
            titles
        )
        viewPager.adapter = chatAdapter
        tabLayout.setupWithViewPager(viewPager)
    }
//    var loadAll=0;
//    fun removeLoading(){
//        if(loadAll==1){
//            view2.findViewById<RelativeLayout>(R.id.loading).visibility = View.GONE
//        }else{
//            loadAll++;
//        }
//
//    }

}
