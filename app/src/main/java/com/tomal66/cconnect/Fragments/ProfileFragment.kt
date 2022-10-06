package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomal66.cconnect.Activities.EditProfileActivity
import com.tomal66.cconnect.Activities.MainActivity
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import java.io.File


class ProfileFragment : Fragment() {
    @BindView(R.id.options)
    lateinit var optionsBtn : ImageView

    @BindView(R.id.username)
    lateinit var username : TextView

    @BindView(R.id.image_profile)
    lateinit var profileImage : ImageView

    @BindView(R.id.posts)
    lateinit var posts : TextView

    @BindView(R.id.followers)
    lateinit var followers : TextView

    @BindView(R.id.following)
    lateinit var following : TextView

    @BindView(R.id.fullname)
    lateinit var fullname : TextView

    @BindView(R.id.bio)
    lateinit var bio : TextView

    private lateinit var user : User
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri : Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_profile, container, false)
            val editProfileBtn: Button = view.findViewById(R.id.edit_profile)

            editProfileBtn.setOnClickListener(){
                val intent = Intent(activity, EditProfileActivity::class.java)
                startActivity(intent)
        }

        //optionsBtn = view.findViewById(R.id.options)

        ButterKnife.bind(this,view)

        optionsBtn.setOnClickListener(){
            (activity as MainActivity).showBottomSheet()
        }
        /*optionsBtn.setOnClickListener(){
            val fragment = OptionsFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container,fragment)?.commit()

        }*/

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getProfile()
    }

    private fun getProfile() {
        getCurrentUser()

    }

    private fun getCurrentUser(){
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance("https://cconnect-2905d-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Users")
        if(currentUserID.isNotEmpty()){
            usersRef.child(currentUserID).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!
                    username.setText(user.username)
                    fullname.setText(user.firstname + " " + user.lastname)
                    bio.setText(user.bio)
                    posts.setText(user.posts.toString())
                    followers.setText(user.followers.toString())
                    following.setText(user.following.toString())
                    storageReference = FirebaseStorage.getInstance().reference.child("Users/$currentUserID")

                    val localFile = File.createTempFile("tempImage","jpg")

                    storageReference.getFile(localFile).addOnSuccessListener {

                        val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
                        profileImage.setImageBitmap(bitmap)


                    }.addOnFailureListener{
                        Toast.makeText(activity,"Failed to retrieve image",Toast.LENGTH_SHORT).show()
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }

    }

}