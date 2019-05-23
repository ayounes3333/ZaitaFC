package com.zaita.aliyounes.zaitafc.chat.activities

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.chat.fragments.ChatsFragment
import com.zaita.aliyounes.zaitafc.databinding.ActivityChatBinding

class ChatsActivity : AppCompatActivity() {

    private var binding: ActivityChatBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        setSupportActionBar(binding!!.chatsBar.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        binding!!.chatsBar.lblTitle.setTextColor(Color.WHITE)
        binding!!.chatsBar.lblTitle.setHintTextColor(Color.WHITE)

        // display chats fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.chat_fragment_container,
                        ChatsFragment(),
                        ChatsFragment.TAG)
                .commit()
    }

    // handle back press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
