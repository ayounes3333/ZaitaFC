package com.zaita.aliyounes.zaitafc.chat.fragments


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.zaita.aliyounes.zaitafc.chat.adapters.ChatsAdapter
import com.zaita.aliyounes.zaitafc.chat.network.ChatsNetworkCalls
import com.zaita.aliyounes.zaitafc.databinding.FragmentChatsBinding
import com.zaita.aliyounes.zaitafc.R

import java.io.IOException

import io.reactivex.disposables.CompositeDisposable

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()

    private var binding: FragmentChatsBinding? = null
    private var adapter: ChatsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        populateFirebaseData()
    }

    private fun setupRecyclerView() {
        adapter = ChatsAdapter(context!!)
        binding!!.recyclerViewChats.adapter = adapter
        binding!!.recyclerViewChats.layoutManager = LinearLayoutManager(context)
        if (adapter!!.items.size == 0) {
            binding!!.empty.visibility = View.VISIBLE
        } else {
            binding!!.empty.visibility = View.GONE
        }
    }

    private fun populateFirebaseData() {
        compositeDisposable.add(ChatsNetworkCalls.chats.subscribe({ dialog ->
            //Success
            adapter!!.manageChildItem(dialog)
            if (adapter!!.items.size == 0) {
                binding!!.empty.visibility = View.VISIBLE
            } else {
                binding!!.empty.visibility = View.GONE
            }
        }, { throwable ->
            //Error
            // print exception stack
            throwable.printStackTrace()
            if (throwable is IOException) {
                // this is most likely a connection error, so we need to show a message toast.
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
            } else {
                // we need to show the exception details in a message toast.
                Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
            }
        }))
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG: String = ChatsFragment::class.java.simpleName
    }
}// Required empty public constructor
