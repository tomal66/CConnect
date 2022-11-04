package com.tomal66.cconnect.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tomal66.cconnect.Activities.ProfileActivity
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import org.jetbrains.annotations.NotNull
import java.io.File

class AddPeopleAdapter (private var mContext: Context,
                        private var userList: ArrayList<User>): RecyclerView.Adapter<AddPeopleAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private lateinit var currUser: User

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return AddPeopleAdapter.ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        var storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("Users/${user.uid}")

        val localFile = File.createTempFile("tempImage","jpg")

        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
            holder.profileImage.setImageBitmap(bitmap)


        }.addOnFailureListener{

        }
        holder.username.text = user.username
        holder.fullname.text = user.firstname + " " + user.lastname
        holder.deptuniv.text = user.department + ", " + user.institution
        holder.city.text = user.city
        Picasso.get().load(user.uid).placeholder(R.drawable.default_user).into(holder.profileImage)

        checkFollowingStatus(user.uid, holder.followBtn)


        usersRef.child(firebaseUser?.uid.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currUser = snapshot.getValue(User::class.java)!!

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        holder.layout.setOnClickListener(){
            val intent = Intent(mContext, ProfileActivity::class.java)
            intent.putExtra("uid", user.uid)
            mContext.startActivity(intent)
        }




        holder.followBtn.setOnClickListener(){
            if(holder.followBtn.text.toString() == "Follow")
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().getReference()
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid.toString())
                        .setValue(true).addOnCompleteListener(){ task->

                            if(task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().getReference()
                                        .child("Follow").child(user.uid.toString())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener(){ task->

                                            if(task.isSuccessful)
                                            {
                                                user.followers = user.followers?.plus(1)
                                                currUser.following = currUser.following?.plus(1)
                                                FirebaseDatabase.getInstance().getReference("Users").child(user.uid.toString()).setValue(user)
                                                FirebaseDatabase.getInstance().getReference("Users").child(currUser.uid.toString()).setValue(currUser)
                                            }

                                        }
                                }
                            }

                        }
                }
            }

            else
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().getReference()
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid.toString())
                        .removeValue().addOnCompleteListener(){ task->

                            if(task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().getReference()
                                        .child("Follow").child(user.uid.toString())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener(){ task->

                                            if(task.isSuccessful)
                                            {
                                                user.followers = user.followers?.minus(1)
                                                currUser.following = currUser.following?.minus(1)
                                                FirebaseDatabase.getInstance().getReference("Users").child(user.uid.toString()).setValue(user)
                                                FirebaseDatabase.getInstance().getReference("Users").child(currUser.uid.toString()).setValue(currUser)
                                            }

                                        }
                                }
                            }

                        }
                }
            }
        }
    }



    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView){

        var profileImage : ImageView = itemView.findViewById(R.id.profile_image)
        var fullname : TextView = itemView.findViewById(R.id.fullname)
        var username : TextView = itemView.findViewById(R.id.username)
        var deptuniv : TextView = itemView.findViewById(R.id.deptuniv)
        var city : TextView = itemView.findViewById(R.id.city)
        var followBtn : Chip = itemView.findViewById(R.id.followBtn)
        var layout : LinearLayout = itemView.findViewById(R.id.user_item)


    }

    private fun checkFollowingStatus(uid: String?, followBtn: Chip) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(uid?.let { snapshot.child(it).exists() } == true)
                {
                    followBtn.text = "Following"
                    followBtn.isChecked = true
                }
                else
                {
                    followBtn.text = "Follow"
                    followBtn.isChecked = false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}