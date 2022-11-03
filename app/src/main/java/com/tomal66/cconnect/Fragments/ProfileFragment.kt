package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomal66.cconnect.Activities.EditProfileActivity
import com.tomal66.cconnect.Activities.MainActivity
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import org.w3c.dom.Text
import java.io.File


class ProfileFragment : Fragment() {
    @BindView(R.id.about)
    lateinit var aboutBtn : ImageButton

    @BindView(R.id.recycler_view_posts)
    lateinit var recycler_view_posts : RecyclerView

    @BindView(R.id.aboutdata)
    lateinit var aboutData : ScrollView

    @BindView(R.id.all_posts)
    lateinit var postsBtn : ImageButton

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

    @BindView(R.id.department)
    lateinit var department : TextView

    @BindView(R.id.university)
    lateinit var university : TextView

    @BindView(R.id.city)
    lateinit var city : TextView

    @BindView(R.id.gender)
    lateinit var gender : TextView

    @BindView(R.id.dob)
    lateinit var dob : TextView

    @BindView(R.id.interests)
    lateinit var interests : TextView


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

        aboutBtn.setOnClickListener(){
            aboutData.visibility = View.VISIBLE
            recycler_view_posts.visibility = View.GONE
        }

        postsBtn.setOnClickListener(){
            aboutData.visibility = View.GONE
            recycler_view_posts.visibility = View.VISIBLE
        }

        optionsBtn.setOnClickListener(){
            (activity as MainActivity).showBottomSheet()
        }

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
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
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
                    department.setText(user.department.toString())
                    university.setText(user.institution.toString())
                    city.setText(user.city + ", " + user.country)
                    gender.setText(user.gender)
                    dob.setText(user.dob)
                    var res = user.interest?.let { TextUtils.join(", ", it) }
                    interests.setText(res)
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

    override fun onResume() {
        super.onResume()
        getCurrentUser()
    }

}