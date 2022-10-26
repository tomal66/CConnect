package com.tomal66.cconnect.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tomal66.cconnect.Fragments.SearchFragment
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import org.jetbrains.annotations.NotNull
import org.w3c.dom.Text
import java.io.File

class SearchAdapter(private var mContext: Context,
                    private var userList: ArrayList<User>,
                    private var isFragment: Boolean = false): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return SearchAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        var storageReference: StorageReference
        storageReference = FirebaseStorage.getInstance().reference.child("Users/${user.uid}")

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
        var followState : TextView = itemView.findViewById(R.id.followState)
        var followBtn : ImageView = itemView.findViewById(R.id.followBtn)


    }
}