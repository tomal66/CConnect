package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tomal66.cconnect.Activities.PersonalityTestActivity
import com.tomal66.cconnect.R

class CreateProfileFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_profile2, container, false)
        val backBtn : Button = view.findViewById(R.id.backBtn)
        val nextBtn : Button = view.findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener(){
            val intent = Intent(activity, PersonalityTestActivity::class.java)
            startActivity(intent)
        }
        backBtn.setOnClickListener(){
            val fragment = CreateProfileFragment1()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.create_profile_nav_container,fragment)?.commit()
        }
        return view
    }

}