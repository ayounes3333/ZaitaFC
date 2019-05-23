package com.zaita.aliyounes.zaitafc.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.zaita.aliyounes.zaitafc.fragments.AllMembersFragment
import com.zaita.aliyounes.zaitafc.fragments.FootballTeamFragment
import com.zaita.aliyounes.zaitafc.fragments.PingPongFragment


/**
 * Created by mohammad.younes on 2/22/2017.
 */

class MemberTabsPagerAdapter(fm: FragmentManager, private val mNumOfTabs: Int) : FragmentPagerAdapter(fm) {

    private val allMembersFragment: AllMembersFragment
    private val footballTeamFragment: FootballTeamFragment
    private val pingPongFragment: PingPongFragment

    init {
        allMembersFragment = AllMembersFragment()
        pingPongFragment = PingPongFragment()
        footballTeamFragment = FootballTeamFragment()
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return allMembersFragment
            1 -> return footballTeamFragment
            2 -> return pingPongFragment
        }
        return null
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}
