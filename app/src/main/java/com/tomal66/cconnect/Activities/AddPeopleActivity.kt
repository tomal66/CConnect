package com.tomal66.cconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tomal66.cconnect.Adapter.AddPeopleAdapter
import com.tomal66.cconnect.Adapter.NewMessageAdapter
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R

class AddPeopleActivity : AppCompatActivity() {
    companion object {
        val TAG = "CheckLog"
    }

    @BindView(R.id.back)
    lateinit var backBtn: ImageView

    @BindView(R.id.recyclerview_add_people)
    lateinit var recyclerview_add_people: RecyclerView

    private var addPeopleAdapter: AddPeopleAdapter?= null
    private var mUser: MutableList<User>?= null
    private var mAuth = FirebaseAuth.getInstance()
    var usersRef = FirebaseDatabase.getInstance().getReference().child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_people)
        ButterKnife.bind(this)

        recyclerview_add_people.setHasFixedSize(true)
        recyclerview_add_people.layoutManager = LinearLayoutManager(this)
        mUser = ArrayList()
        addPeopleAdapter = AddPeopleAdapter(this, mUser as ArrayList<User>)
        recyclerview_add_people.adapter = addPeopleAdapter

        var compRef = FirebaseDatabase.getInstance().getReference().child("Compitability").child(mAuth.uid!!)
        compRef.orderByValue().addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mUser?.clear()

                for(postSnapshot in snapshot.children)
                {
                    Log.d(TAG,postSnapshot.key.toString())
                    usersRef.child(postSnapshot.key.toString()).addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var user = snapshot.getValue(User::class.java)!!
                            if(mAuth.currentUser?.uid!= user.uid){
                                mUser?.add(user)
                                addPeopleAdapter!!.notifyDataSetChanged()
                            }

                        }
                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }



            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}