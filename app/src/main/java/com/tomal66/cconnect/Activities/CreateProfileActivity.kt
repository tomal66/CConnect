package com.tomal66.cconnect.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tomal66.cconnect.Fragments.CreateProfileFragment1
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R

class CreateProfileActivity : AppCompatActivity() {

    public lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        val cpf1 = CreateProfileFragment1()

        supportFragmentManager.beginTransaction().replace(R.id.create_profile_nav_container, cpf1).commit()
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

}