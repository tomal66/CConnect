package com.tomal66.cconnect.Activities

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import java.io.File

class ProfileActivity : AppCompatActivity() {
    @BindView(R.id.about)
    lateinit var aboutBtn : ImageButton

    @BindView(R.id.recycler_view_posts)
    lateinit var recycler_view_posts : RecyclerView

    @BindView(R.id.aboutdata)
    lateinit var aboutData : ScrollView

    @BindView(R.id.all_posts)
    lateinit var postsBtn : ImageButton

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

    @BindView(R.id.followBtn)
    lateinit var followBtn : Chip

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


    private var user : User? = null
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri : Uri

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private lateinit var currUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        ButterKnife.bind(this)
        val uid = intent.getStringExtra("uid")

        if (uid != null) {
            getUser(uid)
        }

        checkFollowingStatus(uid, followBtn)

        usersRef.child(firebaseUser?.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currUser = snapshot.getValue(User::class.java)!!

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        aboutBtn.setOnClickListener(){
            aboutData.visibility = View.VISIBLE
            recycler_view_posts.visibility = View.GONE
        }

        postsBtn.setOnClickListener(){
            aboutData.visibility = View.GONE
            recycler_view_posts.visibility = View.VISIBLE
        }

        followBtn.setOnClickListener(){
            if(followBtn.text.toString() == "Follow")
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().getReference()
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user?.uid.toString())
                        .setValue(true).addOnCompleteListener(){ task->

                            if(task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().getReference()
                                        .child("Follow").child(user?.uid.toString())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener(){ task->

                                            if(task.isSuccessful)
                                            {
                                                user?.followers = user?.followers?.plus(1)
                                                currUser.following = currUser.following?.plus(1)
                                                FirebaseDatabase.getInstance().getReference("Users").child(
                                                    user?.uid.toString()).setValue(user)
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
                        .child("Following").child(user?.uid.toString())
                        .removeValue().addOnCompleteListener(){ task->

                            if(task.isSuccessful)
                            {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().getReference()
                                        .child("Follow").child(user?.uid.toString())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener(){ task->

                                            if(task.isSuccessful)
                                            {
                                                user?.followers = user?.followers?.minus(1)
                                                currUser.following = currUser.following?.minus(1)
                                                FirebaseDatabase.getInstance().getReference("Users").child(
                                                    user?.uid.toString()).setValue(user)
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

    private fun getUser(uid : String){
        val currentUserID = uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        if(currentUserID.isNotEmpty()){
            usersRef.child(currentUserID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    user = snapshot.getValue(User::class.java)!!
                    username.setText(user!!.username)
                    fullname.setText(user!!.firstname + " " + user!!.lastname)
                    bio.setText(user!!.bio)
                    posts.setText(user!!.posts.toString())
                    followers.setText(user!!.followers.toString())
                    following.setText(user!!.following.toString())
                    department.setText(user!!.department.toString())
                    university.setText(user!!.institution.toString())
                    city.setText(user!!.city + ", " + user!!.country)
                    gender.setText(user!!.gender)
                    dob.setText(user!!.dob)
                    var res = user!!.interest?.let { TextUtils.join(", ", it) }
                    interests.setText(res)

                    storageReference = FirebaseStorage.getInstance().reference.child("Users/$currentUserID")

                    val localFile = File.createTempFile("tempImage","jpg")

                    storageReference.getFile(localFile).addOnSuccessListener {

                        val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
                        profileImage.setImageBitmap(bitmap)


                    }.addOnFailureListener{
                        Toast.makeText(this@ProfileActivity,"Failed to retrieve image", Toast.LENGTH_SHORT).show()
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }

    }
}