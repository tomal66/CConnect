package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.tomal66.cconnect.Activities.AddPeopleActivity
import com.tomal66.cconnect.Activities.ChatActivity
import com.tomal66.cconnect.Activities.EditProfileActivity
import com.tomal66.cconnect.R


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val messages:ImageView = view.findViewById(R.id.messages)
        messages.setOnClickListener(){
            val intent = Intent(activity, ChatActivity::class.java)
            startActivity(intent)
        }


        val addPeople:ImageView = view.findViewById(R.id.add_people)
        addPeople.setOnClickListener(){
            val intent = Intent(activity, AddPeopleActivity::class.java)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }

}