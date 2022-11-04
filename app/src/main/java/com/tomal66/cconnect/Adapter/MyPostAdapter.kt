package com.tomal66.cconnect.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.tomal66.cconnect.Model.Post
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.annotations.NotNull
import java.io.File

class MyPostAdapter(private val mContext: Context,
                    private val mPost: List<Post>): RecyclerView.Adapter<MyPostAdapter.ViewHolder>(){

    private var firebaseUser : FirebaseUser? = null

    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.my_post_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: MyPostAdapter.ViewHolder, position: Int) {
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



        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserID)

        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)!!

                val storageReference = FirebaseStorage.getInstance().reference.child("Users/${currentUserID}")


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
        var postImage : ImageView = itemView.findViewById(R.id.post_Image)
        var likeBtn: ImageView = itemView.findViewById(R.id.likeBtn)
        var threeDot: ImageView = itemView.findViewById(R.id.threeDot)
        var userName: TextView?= itemView.findViewById(R.id.user_Name)
        var postTitle: TextView = itemView.findViewById(R.id.post_Title)
        var postDescription: TextView = itemView.findViewById(R.id.post_Description)
        var likes: TextView = itemView.findViewById(R.id.likes)

    }

}