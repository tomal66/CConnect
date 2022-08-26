package com.tomal66.cconnect.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tomal66.cconnect.Fragments.CreateProfileFragment1
import com.tomal66.cconnect.R

class CreateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        supportFragmentManager.beginTransaction().replace(R.id.create_profile_nav_container, CreateProfileFragment1()).commit()
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}