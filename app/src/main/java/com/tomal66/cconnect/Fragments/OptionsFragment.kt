package com.tomal66.cconnect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.tomal66.cconnect.R

class OptionsFragment : Fragment() {

    private lateinit var back:ImageView
    private lateinit var account_preferences:RelativeLayout
    private lateinit var security:RelativeLayout
    private lateinit var visibility:RelativeLayout
    private lateinit var communications:RelativeLayout
    private lateinit var privacy:RelativeLayout
    private lateinit var report:RelativeLayout
    private lateinit var logout:RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_options, container, false)

        back = view.findViewById(R.id.back)
        account_preferences = view.findViewById(R.id.account_preferences)
        security = view.findViewById(R.id.security)
        visibility = view.findViewById(R.id.visibility)
        communications = view.findViewById(R.id.communications)
        privacy = view.findViewById(R.id.privacy)
        report = view.findViewById(R.id.report)
        logout = view.findViewById(R.id.logout)

        back.setOnClickListener(){
            val fragment = ProfileFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container,fragment)?.commit()
        }



        return view
    }

}