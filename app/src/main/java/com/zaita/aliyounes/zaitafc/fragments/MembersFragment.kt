package com.zaita.aliyounes.zaitafc.fragments


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.adapters.MemberTabsPagerAdapter


/**
 * A simple [Fragment] subclass.
 */
class MembersFragment : Fragment() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_members, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupTabs()
    }

    private fun setupViews(rootView: View) {

        tabLayout = rootView.findViewById(R.id.tab_layout_activities)
        viewPager = rootView.findViewById(R.id.pager)
    }

    private fun setupTabs() {
        tabLayout!!.removeAllTabs()
        tabLayout!!.addTab(tabLayout!!.newTab().setText(R.string.tab_all))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(R.string.tab_football))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(R.string.tab_pingpong))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL


        val adapter = MemberTabsPagerAdapter(activity!!.supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            setupTabs()
        }
    }

    companion object {
        val TAG: String = MembersFragment::class.java.simpleName
    }
}// Required empty public constructor
