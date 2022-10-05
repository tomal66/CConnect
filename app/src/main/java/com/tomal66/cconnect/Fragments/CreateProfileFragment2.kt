package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tomal66.cconnect.Activities.CreateProfileActivity
import com.tomal66.cconnect.Activities.PersonalityTestActivity
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R

class CreateProfileFragment2 : Fragment() {

    @BindView(R.id.editBio)
    lateinit var editBio : EditText

    @BindView(R.id.tv_change)
    lateinit var changeBtn : TextView

    //var user: User = (activity as CreateProfileActivity).user

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_profile2, container, false)

        val backBtn : Button = view.findViewById(R.id.backBtn)
        val nextBtn : Button = view.findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener(){

            //createPrimaryProfile()


            val intent = Intent(activity, PersonalityTestActivity::class.java)
            startActivity(intent)
        }
        backBtn.setOnClickListener(){
            val fragment = CreateProfileFragment1()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.create_profile_nav_container,fragment)?.commit()
        }
        return view
    }

    private fun createPrimaryProfile() {
        var bio = editBio.text.toString()
        //user.bio = bio

        //saveUserInfo(user)
    }

    private fun saveUserInfo(user: User) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["firstname"] = currentUserID
        userMap["lastname"] = currentUserID
        userMap["age"] = currentUserID
        userMap["gender"] = currentUserID
        userMap["institution"] = currentUserID
        userMap["department"] = currentUserID
        userMap["country"] = currentUserID
        userMap["bio"] = currentUserID
    }

}