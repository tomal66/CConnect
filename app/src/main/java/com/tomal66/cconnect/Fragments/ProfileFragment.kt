package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.tomal66.cconnect.Activities.EditProfileActivity
import com.tomal66.cconnect.Activities.PersonalityTestActivity
import com.tomal66.cconnect.R

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val editProfileBtn: Button = view.findViewById(R.id.edit_profile)
        editProfileBtn.setOnClickListener(){
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}