package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.tomal66.cconnect.Activities.EditProfileActivity
import com.tomal66.cconnect.Activities.MainActivity
import com.tomal66.cconnect.R


class ProfileFragment : Fragment() {
    private lateinit var optionsBtn : ImageView

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

        optionsBtn = view.findViewById(R.id.options)
        optionsBtn.setOnClickListener(){
            val fragment = OptionsFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container,fragment)?.commit()

        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}