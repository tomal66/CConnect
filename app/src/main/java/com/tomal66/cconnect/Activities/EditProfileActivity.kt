package com.tomal66.cconnect.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
//import com.rengwuxian.materialedittext.MaterialEditText
import com.tomal66.cconnect.R

class EditProfileActivity : AppCompatActivity() {
    private lateinit var close : ImageView
    private lateinit var image_profile : ImageView
    private lateinit var save : ImageView
    private lateinit var tv_change : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        close = findViewById(R.id.close)
        close.setOnClickListener(){
            finish()
        }
    }
}