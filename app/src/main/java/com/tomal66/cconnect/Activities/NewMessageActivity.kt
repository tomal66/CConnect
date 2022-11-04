package com.tomal66.cconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tomal66.cconnect.Adapter.NewMessageAdapter
import com.tomal66.cconnect.Adapter.SearchAdapter
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class NewMessageActivity : AppCompatActivity() {

    private var recyclerview_NewMessage : RecyclerView?=null
    private var newMessageAdapter: NewMessageAdapter?= null
    private var mUser: MutableList<User>?= null
    private var mAuth = FirebaseAuth.getInstance()
    var usersRef = FirebaseDatabase.getInstance().getReference().child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
        recyclerview_NewMessage = findViewById(R.id.recyclerview_NewMessage)
        recyclerview_NewMessage?.setHasFixedSize(true)
        recyclerview_NewMessage?.layoutManager = LinearLayoutManager(this)

        mUser = ArrayList()
        newMessageAdapter = NewMessageAdapter(this, mUser as ArrayList<User>)
        recyclerview_NewMessage?.adapter = newMessageAdapter

        usersRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUser?.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid!= currentUser?.uid){
                        mUser?.add(currentUser!!)
                    }
                }
                newMessageAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
