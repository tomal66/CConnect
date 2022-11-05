package com.tomal66.cconnect.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tomal66.cconnect.Activities.ChatLogActivity
import com.tomal66.cconnect.Model.Notification
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import org.jetbrains.annotations.NotNull
import java.io.File

class NotificationAdapter(private var mContext: Context,
                          private var mNotification: ArrayList<Notification>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private lateinit var currUser: User



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return mNotification.size
    }

    inner class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView){

        var image_profile : ImageView = itemView.findViewById(R.id.image_profile)
        var post_image : ImageView = itemView.findViewById(R.id.post_image)
        var user_name : TextView ?= itemView.findViewById(R.id.username)
        var text : TextView = itemView.findViewById(R.id.comment)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val noti = mNotification[position]


        // liker image setting
        val storageReference = FirebaseStorage.getInstance().reference.child("Users")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.child("${noti.userId}").getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
            holder.image_profile.setImageBitmap(bitmap)
        }.addOnFailureListener {

        }

        // post image setting
        val postref = FirebaseStorage.getInstance().reference.child("Posts")

        val localFile1 = File.createTempFile("tempImage", "jpg")
        postref.child("${noti.postid}").getFile(localFile1).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile((localFile1.absolutePath))
            holder.post_image.setImageBitmap(bitmap)

        }.addOnFailureListener {

        }
        // liked text
//        holder.text.text = noti.text

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(noti.userId.toString())

        userRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val user1 = snapshot.getValue(User::class.java)

                if (user1 != null) {
                    holder.text.text = user1.firstname + " " + user1.lastname + " " + noti.text
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //

    }
}