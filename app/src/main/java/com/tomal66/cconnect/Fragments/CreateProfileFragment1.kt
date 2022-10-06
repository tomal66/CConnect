package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tomal66.cconnect.Activities.CreateProfileActivity
import com.tomal66.cconnect.Activities.PersonalityTestActivity
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R

class CreateProfileFragment1 : Fragment() {

    @BindView(R.id.editUsername)
    lateinit var editUserame : EditText

    @BindView(R.id.editFirstName)
    lateinit var editFirstName : EditText

    @BindView(R.id.editLastName)
    lateinit var editLastName : EditText

    @BindView(R.id.editAge)
    lateinit var editAge : EditText

    @BindView(R.id.gender)
    lateinit var editGender : Spinner

    @BindView(R.id.editInstitution)
    lateinit var editInstituation : EditText

    @BindView(R.id.editDepartment)
    lateinit var editDepartment : EditText

    @BindView(R.id.editCity)
    lateinit var editCity : EditText

    @BindView(R.id.editCountry)
    lateinit var editCountry : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_profile1, container, false)

        ButterKnife.bind(this,view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nextBtn : Button = view.findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener(){
            createPrimaryProfile(view)
        }
    }

    fun createPrimaryProfile(view: View) {
        var username : String = editUserame.text.toString()
        var firstname : String = editFirstName.text.toString()
        var lastname : String = editLastName.text.toString()
        var age : String = editAge.text.toString()
        var gender : String = editGender.selectedItem.toString()
        var institution : String = editInstituation.text.toString()
        var department : String = editDepartment.text.toString()
        var city : String = editCity.text.toString()
        var country : String = editCountry.text.toString()

        when{
            TextUtils.isEmpty(username) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(firstname) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(lastname) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(age) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(gender) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(institution) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(department) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(city) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(country) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()

            else -> {
                val user = User(username, firstname, lastname, age, gender, institution, department, city, country)
                saveUserInfo(user)

                /*val fragment = CreateProfileFragment2()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.create_profile_nav_container,fragment)?.commit()*/

            }
        }
    }

    private fun saveUserInfo(user: User) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance("https://cconnect-2905d-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["username"] = user.username.toString()
        userMap["firstname"] = user.firstname.toString()
        userMap["lastname"] = user.lastname.toString()
        userMap["age"] = user.age.toString()
        userMap["gender"] = user.gender.toString()
        userMap["institution"] = user.institution.toString()
        userMap["department"] = user.department.toString()
        userMap["country"] = user.country.toString()
        userMap["bio"] = "New Account"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/cconnect-2905d.appspot.com/o/Defaults%2Fuser.jpg?alt=media&token=988f1e85-a303-459b-aaca-11db3e6017a7"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    val intent = Intent(activity, PersonalityTestActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(activity,"Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

}