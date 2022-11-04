package com.tomal66.cconnect.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tomal66.cconnect.Activities.ChatLogActivity
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import org.jetbrains.annotations.NotNull
import java.io.File

class NewMessageAdapter(private var mContext: Context,
                        private var userList: ArrayList<User>): RecyclerView.Adapter<NewMessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_row_new_message, parent, false)
        return NewMessageAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView){

        var imageView : ImageView = itemView.findViewById(R.id.imageView)
        var fullName : TextView = itemView.findViewById(R.id.fullName)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        var storageReference: StorageReference
        storageReference = FirebaseStorage.getInstance().reference.child("Users/${user.uid}")

        val localFile = File.createTempFile("tempImage","jpg")

        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
            holder.imageView.setImageBitmap(bitmap)

        }.addOnFailureListener{

        }

        holder.fullName.text = user.firstname + " " + user.lastname
        Picasso.get().load(user.uid).placeholder(R.drawable.default_user).into(holder.imageView)
        holder.fullName.setOnClickListener{
            val intent = Intent(mContext, ChatLogActivity::class.java)
            intent.putExtra("name", user.firstname + user.lastname)
            intent.putExtra("uid", user.uid)
            mContext.startActivity(intent);

           //finish()
            //manifest kichu mal change kore dekhte paro error ashle
            //check time around 7mins 50 secs
        }
    }


}