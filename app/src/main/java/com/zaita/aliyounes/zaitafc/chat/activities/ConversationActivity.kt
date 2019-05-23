package com.zaita.aliyounes.zaitafc.chat.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.chat.fragments.ConversationFragment
import com.zaita.aliyounes.zaitafc.databinding.ActivityConversationBinding

class ConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityConversationBinding>(this, R.layout.activity_conversation)
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        // create and display conversations fragment
        if (intent.extras != null) {
            binding.lblToolbarTitle.text = intent.extras!!.getString("chatName")
            val fragment = ConversationFragment()
            // add arguments from intent
            fragment.arguments = intent.extras
            // Display Conversations fragment
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.chat_fragment_container,
                            fragment,
                            ConversationFragment.TAG)
                    .commit()
        }
    }

    // handle toolbar back press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
