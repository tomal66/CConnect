package com.tomal66.cconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.tomal66.cconnect.R

class ChatActivity : AppCompatActivity() {
    private lateinit var back:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        back = findViewById(R.id.back)
        back.setOnClickListener(){
            finish()
        }

    }
}