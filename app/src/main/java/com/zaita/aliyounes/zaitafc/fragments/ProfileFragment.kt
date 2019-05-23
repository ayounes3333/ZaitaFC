package com.zaita.aliyounes.zaitafc.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private var textViewPosition: TextView? = null
    private var textViewEmail: TextView? = null
    private var textViewAge: TextView? = null
    private var textViewGender: TextView? = null
    private var toolbar: Toolbar? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
    }

    private fun setupViews(rootView: View) {
        textViewPosition = rootView.findViewById(R.id.textView_bioText)
        textViewEmail = rootView.findViewById(R.id.textView_emailText)
        textViewAge = rootView.findViewById(R.id.textView_ageText)
        textViewGender = rootView.findViewById(R.id.textView_genderText)
        toolbar = rootView.findViewById(R.id.toolbar)
        toolbar!!.title = PrefUtils.Session.userName
        textViewPosition!!.text = PrefUtils.Session.position
        textViewAge!!.text = PrefUtils.Session.age.toString()
        textViewEmail!!.text = PrefUtils.Session.userEmail
        textViewGender!!.text = if (PrefUtils.Session.genderId == 1) getString(R.string.gender_male) else getString(R.string.gender_female)
    }

    companion object {
        val TAG: String = ProfileFragment::class.java.simpleName
    }
}// Required empty public constructor
