package com.zaita.aliyounes.zaitafc.chat.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.polyak.iconswitch.IconSwitch
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.chat.adapters.UsersAdapter
import com.zaita.aliyounes.zaitafc.databinding.ActivityNewChatsBinding
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils
import com.zaita.aliyounes.zaitafc.network.UserNetworkCalls
import com.zaita.aliyounes.zaitafc.pojos.UserDetails
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException

class NewChatsActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var adapter: UsersAdapter
    private lateinit var binding: ActivityNewChatsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chats)
    }

    private fun setupViews() {
        binding.iconSwitch.setCheckedChangeListener { checked ->
            if(checked == IconSwitch.Checked.LEFT) {
                binding.relativeLayoutGroup.visibility = View.GONE
                binding.textInputGroupName.visibility = View.GONE
            } else {
                binding.relativeLayoutGroup.visibility = View.VISIBLE
                binding.textInputGroupName.visibility = View.VISIBLE
            }
        }

        binding.fabDone.setOnClickListener {

        }
    }

    private fun populateData() {
        adapter = UsersAdapter(this, false)
        compositeDisposable.add(UserNetworkCalls.users.subscribe({ data ->
            //Success
            val user = data.value.getValue(UserDetails::class.java)
            if (user != null && !user.email.equals(PrefUtils.Session.userEmail, false)) {
                adapter.manageChildItem(data)
            }
        }, { throwable ->
            //Error
            // print exception stack
            throwable.printStackTrace()
            if (throwable is IOException) {
                // this is most likely a connection error, so we need to show a message toast.
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show()
            } else {
                // we need to show the exception details in a message toast.
                Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                throwable.printStackTrace()
            }
        }))
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
