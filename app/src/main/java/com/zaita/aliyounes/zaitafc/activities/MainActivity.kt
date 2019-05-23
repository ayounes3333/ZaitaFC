package com.zaita.aliyounes.zaitafc.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.fragments.MembersFragment
import com.zaita.aliyounes.zaitafc.fragments.NewsFragment
import com.zaita.aliyounes.zaitafc.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = { item:MenuItem ->
        when (item.itemId) {
            R.id.navigation_news -> {
                showNewsFragment()
            }
            R.id.navigation_members -> {
                showMembersFragment()
            }
            R.id.navigation_profile -> {
                showProfileFragment()
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        showNewsFragment()
    }

    private fun showNewsFragment() {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(NewsFragment.TAG)
        if (fragment == null)
            fragment = NewsFragment()
        supportFragmentManager
                .beginTransaction()
                .addToBackStack(NewsFragment.TAG)
                .replace(R.id.fragment_container,
                        fragment,
                        NewsFragment.TAG)
                .commit()
    }

    private fun showMembersFragment() {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(MembersFragment.TAG)
        if (fragment == null)
            fragment = MembersFragment()
        supportFragmentManager
                .beginTransaction()
                .addToBackStack(MembersFragment.TAG)
                .replace(R.id.fragment_container,
                        fragment,
                        MembersFragment.TAG)
                .commit()
    }

    private fun showProfileFragment() {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(ProfileFragment.TAG)
        if (fragment == null)
            fragment = ProfileFragment()
        supportFragmentManager
                .beginTransaction()
                .addToBackStack(ProfileFragment.TAG)
                .replace(R.id.fragment_container,
                        fragment,
                        ProfileFragment.TAG)
                .commit()
    }
}
