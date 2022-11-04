package com.tomal66.cconnect.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import java.io.File
import java.lang.Math.abs

class InterestsActivity : AppCompatActivity() {
    @BindView(R.id.chip1)
    lateinit var chip1 : Chip
    @BindView(R.id.chip2)
    lateinit var chip2 : Chip
    @BindView(R.id.chip3)
    lateinit var chip3 : Chip
    @BindView(R.id.chip4)
    lateinit var chip4 : Chip
    @BindView(R.id.chip5)
    lateinit var chip5 : Chip
    @BindView(R.id.chip6)
    lateinit var chip6 : Chip
    @BindView(R.id.chip7)
    lateinit var chip7 : Chip
    @BindView(R.id.chip8)
    lateinit var chip8 : Chip
    @BindView(R.id.chip9)
    lateinit var chip9 : Chip
    @BindView(R.id.chip10)
    lateinit var chip10 : Chip
    @BindView(R.id.chip11)
    lateinit var chip11 : Chip
    @BindView(R.id.chip12)
    lateinit var chip12 : Chip
    @BindView(R.id.chip13)
    lateinit var chip13 : Chip
    @BindView(R.id.chip14)
    lateinit var chip14 : Chip
    @BindView(R.id.chip15)
    lateinit var chip15 : Chip
    @BindView(R.id.chip16)
    lateinit var chip16 : Chip
    @BindView(R.id.chip17)
    lateinit var chip17 : Chip
    @BindView(R.id.chip18)
    lateinit var chip18 : Chip

    private lateinit var doneBtn:Button
    private lateinit var user: User
    private val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests)
        ButterKnife.bind(this)

        usersRef.child(currentUserID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                user = snapshot.getValue(User::class.java)!!
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        doneBtn = findViewById(R.id.doneBtn)
        doneBtn.setOnClickListener(){
            setData()
            calculateDistance()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateDistance() {

        var usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot in dataSnapshot.children)
                {
                    val userC = snapshot.getValue(User::class.java)
                    if(userC!=null)
                    {
                        writeValue(userC)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun writeValue(userC: User)
    {
        var res = 0

        for(i in user.personality?.indices!!)
        {
            res+= abs(user.personality!![i] - userC.personality!![i])
        }

        var compRef = FirebaseDatabase.getInstance().getReference().child("Compitability")

        compRef.child(user.uid!!).child(userC.uid!!).setValue(res)
        compRef.child(userC.uid!!).child(user.uid!!).setValue(res)
    }

    private fun setData() {
        val list: ArrayList<String> = ArrayList()
        if(chip1.isChecked)
        {
            list.add("Animals")
        }
        else
        {
            list.remove("Animals")
        }
        if(chip2.isChecked)
        {
            list.add("Art")
        }
        else
        {
            list.remove("Ary")
        }
        if(chip3.isChecked)
        {
            list.add("DIY")
        }
        else
        {
            list.remove("DIY")
        }
        if(chip4.isChecked)
        {
            list.add("Electronics")
        }
        else
        {
            list.remove("Electronics")
        }
        if(chip5.isChecked)
        {
            list.add("Entertainment")
        }
        else
        {
            list.remove("Entertainment")
        }
        if(chip6.isChecked)
        {
            list.add("Fashion")
        }
        else
        {
            list.remove("Fashion")
        }
        if(chip7.isChecked)
        {
            list.add("Food")
        }
        else
        {
            list.remove("Food")
        }
        if(chip8.isChecked)
        {
            list.add("Festivals")
        }
        else
        {
            list.remove("Festivals")
        }
        if(chip9.isChecked)
        {
            list.add("Gaming")
        }
        else
        {
            list.remove("Gaming")
        }
        if(chip10.isChecked)
        {
            list.add("Health")
        }
        else
        {
            list.remove("Health")
        }
        if(chip11.isChecked)
        {
            list.add("Music")
        }
        else
        {
            list.remove("Music")
        }
        if(chip12.isChecked)
        {
            list.add("Memes")
        }
        else
        {
            list.remove("Memes")
        }
        if(chip13.isChecked)
        {
            list.add("Outdoors")
        }
        else
        {
            list.remove("Outdoors")
        }
        if(chip14.isChecked)
        {
            list.add("Programming")
        }
        else
        {
            list.remove("Programming")
        }
        if(chip15.isChecked)
        {
            list.add("Photography")
        }
        else
        {
            list.remove("Photography")
        }
        if(chip16.isChecked)
        {
            list.add("Science")
        }
        else
        {
            list.remove("Science")
        }
        if(chip17.isChecked)
        {
            list.add("Travel")
        }
        else
        {
            list.remove("Travel")
        }
        if(chip18.isChecked)
        {
            list.add("Writing")
        }
        else
        {
            list.remove("Writing")
        }
        user.interest = list
        FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).setValue(user)

    }
}