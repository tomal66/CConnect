package com.tomal66.cconnect.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.tomal66.cconnect.R

class SignupActivity : AppCompatActivity() {
    private lateinit var nextBtn : Button
    private lateinit var signInBtn : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        nextBtn = findViewById(R.id.nextBtn)
        signInBtn = findViewById(R.id.signInBtn)

        nextBtn.setOnClickListener(){
            val intent = Intent(this, CreateProfileActivity::class.java)
            startActivity(intent)
        }
        signInBtn.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}