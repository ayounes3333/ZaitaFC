package com.zaita.aliyounes.zaitafc.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.adapters.MembersAdapter
import com.zaita.aliyounes.zaitafc.helpers.TestHelper


/**
 * A simple [Fragment] subclass.
 */
class FootballTeamFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: MembersAdapter? = null
    private fun setupRecyclerView(rootView: View) {
        recyclerView = rootView.findViewById(R.id.recyclerView_footballTeam)
        adapter = MembersAdapter(TestHelper.dummyFootballPlayers)
        recyclerView!!.adapter = adapter
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_football_team, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
    }
}// Required empty public constructor
