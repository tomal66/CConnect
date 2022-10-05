package com.tomal66.cconnect.Fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.tomal66.cconnect.Activities.CreateProfileActivity
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R

class CreateProfileFragment1 : Fragment() {
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
        val nextBtn : Button = view.findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener(){

            //createPrimaryProfile()
            val fragment = CreateProfileFragment2()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.create_profile_nav_container,fragment)?.commit()

        }
        ButterKnife.bind(this,view)
        return view
    }

    fun createPrimaryProfile(){
        var firstname : String = editFirstName.text.toString()
        var lastname : String = editLastName.text.toString()
        var age : String = editAge.text.toString()
        var gender : String = editGender.selectedItem.toString()
        var institution : String = editInstituation.text.toString()
        var department : String = editDepartment.text.toString()
        var city : String = editCity.text.toString()
        var country : String = editCountry.text.toString()

        when{
            TextUtils.isEmpty(firstname) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(lastname) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(age) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(gender) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(institution) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(department) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(city) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(country) -> Toast.makeText(activity, "Fields cannot be empty!", Toast.LENGTH_LONG).show()

            else -> {
                val user = User(firstname, lastname, age, gender, institution, department, city, country)
                (activity as CreateProfileActivity).user = user

                val fragment = CreateProfileFragment2()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.create_profile_nav_container,fragment)?.commit()

            }
        }
    }

}