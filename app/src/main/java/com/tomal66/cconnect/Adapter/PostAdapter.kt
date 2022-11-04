package com.tomal66.cconnect.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.tomal66.cconnect.Activities.AddPeopleActivity.Companion.TAG
import com.tomal66.cconnect.Model.Post
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.annotations.NotNull
import java.io.File

class PostAdapter
    (private val mContext: Context,
     private val mPost: List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        firebaseUser = FirebaseAuth.getInstance().currentUser
        holder.postImage.visibility = GONE
        val post = mPost[position]

        // post er chobi dekhabe
        val storageReference = FirebaseStorage.getInstance().reference.child("Posts")
        val localFile = File.createTempFile("tempImage","jpg")
        storageReference.child("${post.pid}").getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
            holder.postImage.setImageBitmap(bitmap)
            holder.postImage.visibility = VISIBLE
        }.addOnFailureListener{

        }
// post er title and description
        holder.postTitle.text = post.title
        holder.postDescription.text = post.description

        isLiked(post.pid!!, holder.likeBtn)
        nrLikes(holder.likes,post.pid!!)

        holder.likeBtn.setOnClickListener(){

            if(holder.likeBtn.getTag().equals("like")){

                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.pid!!)
                    .child(currentUser!!.uid).setValue(true)
                Log.d(TAG, "Clicked")

            }
            else
            {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.pid!!)
                    .child(currentUser!!.uid).removeValue()
            }
        }


        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(post.postedBy.toString())

        usersRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                    val user = snapshot.getValue(User::class.java)!!

                    val storageReference = FirebaseStorage.getInstance().reference.child("Users/${post.postedBy.toString()}")


                    val localFile = File.createTempFile("tempImage2","jpg")

                    storageReference.getFile(localFile).addOnSuccessListener {

                        val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))

                        holder.profileImage?.setImageBitmap(bitmap)
                    }.addOnFailureListener{
                    }

                    if (user != null) {
                        holder.userName?.text = user.firstname+" "+user.lastname
                    }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }



    inner class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage : CircleImageView? = itemView.findViewById(R.id.user_Image)
        var postImage : ImageView = itemView.findViewById(R.id.postImage)
        var likeBtn: ImageView = itemView.findViewById(R.id.likeBtn)
        var threeDot: ImageView = itemView.findViewById(R.id.threeDot)
        var userName: TextView ?= itemView.findViewById(R.id.userName)
        var postTitle: TextView = itemView.findViewById(R.id.postTitle)
        var postDescription: TextView = itemView.findViewById(R.id.postDescription)
        var likes: TextView = itemView.findViewById(R.id.likes)
    }

    private fun isLiked(pid: String, iv: ImageView)
    {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(pid)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(currentUser!!.uid).exists()){
                    iv.setImageResource(R.drawable.ic_liked)
                    iv.tag = "liked"
                }
                else
                {
                    iv.setImageResource(R.drawable.ic_like)
                    iv.tag = "like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun nrLikes(likes: TextView, pid: String)
    {
        val ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(pid)

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                likes.text = snapshot.childrenCount.toString() + " bolts"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


}