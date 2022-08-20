package com.tomal66.cconnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class CreateProfileFragment1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_profile1, container, false)
        val nextBtn : Button = view.findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener(){
            val fragment = CreateProfileFragment2()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.create_profile_nav_container,fragment)?.commit()
        }
        return view
    }

}