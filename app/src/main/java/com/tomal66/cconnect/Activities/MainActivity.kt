package com.tomal66.cconnect.Activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tomal66.cconnect.*
import com.tomal66.cconnect.Fragments.HomeFragment
import com.tomal66.cconnect.Fragments.NotificationFragment
import com.tomal66.cconnect.Fragments.ProfileFragment
import com.tomal66.cconnect.Fragments.SearchFragment
import com.tomal66.cconnect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())


        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_search -> replaceFragment(SearchFragment())
                R.id.nav_heart -> replaceFragment(NotificationFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
                else -> {

                }

            }
            true
        }


    }

    private  fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.commit()
    }

    override fun onStart() {
        super.onStart()

    }

}