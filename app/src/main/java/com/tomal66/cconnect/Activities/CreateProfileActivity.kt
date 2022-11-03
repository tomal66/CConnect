package com.tomal66.cconnect.Activities

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class CreateProfileActivity : AppCompatActivity() {

    lateinit var user : User

    @BindView(R.id.editUsername)
    lateinit var editUserame : EditText

    @BindView(R.id.editFirstName)
    lateinit var editFirstName : EditText

    @BindView(R.id.editLastName)
    lateinit var editLastName : EditText

    @BindView(R.id.editDOB)
    lateinit var editDOB : EditText

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

    private lateinit var storageReference: StorageReference
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        ButterKnife.bind(this)

        val dialog = ProgressDialog(this@CreateProfileActivity)

        dialog.setCancelable(false)
        dialog.setTitle("Creating Profile")
        dialog.setMessage("Please wait...")
        dialog.setCanceledOnTouchOutside(false)


        val nextBtn : Button = findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener(){
            dialog.show()
            createPrimaryProfile()
//            dialog.dismiss()
        }

        val c = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener(){view, year, month, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "MM/dd/yy"
            val sdf = SimpleDateFormat(format, Locale.US)
            editDOB.setText(sdf.format(c.time))
        }

        editDOB.setOnClickListener(){
            DatePickerDialog(this, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setDate() {
        val c = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener(){view, year, month, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "MM/dd/yy"
            val sdf = SimpleDateFormat(format, Locale.US)
            editDOB.setText(sdf.format(c.time))
        }

        /*val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, year, monthOfYear, dayOfMonth ->
            val returnDate = "${monthOfYear + 1} $dayOfMonth $year"
            val date = StringHelper.parseDate(
                "MM dd yyyy",
                "MM/dd/yyyy",
                returnDate
            )
            editDOB.text = StringHelper.parseDate("MM/dd/yyyy", "MM dd yyyy", date)
            editDOB.error = null
        }, year-30, month, day)*/

    }



    private fun createPrimaryProfile() {
        var username : String = editUserame.text.toString()
        var firstname : String = editFirstName.text.toString()
        var lastname : String = editLastName.text.toString()
        var dob : String = editDOB.text.toString()
        var gender : String = editGender.selectedItem.toString()
        var institution : String = editInstituation.text.toString()
        var department : String = editDepartment.text.toString()
        var city : String = editCity.text.toString()
        var country : String = editCountry.text.toString()

        when{
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(firstname) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(lastname) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(dob) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(gender) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(institution) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(department) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(city) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(country) -> Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show()

            else -> {
                val bio : String = "Hey there, I'm using OyeAmigo!"
                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
                val fullname = (firstname+" "+ lastname).toLowerCase()
                var personality : ArrayList<Int> = arrayListOf<Int>(3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3)
                val list: ArrayList<String> = ArrayList()
                val user = User(username,fullname, firstname, lastname, dob, gender, institution, department, city, country, bio, currentUserID,
                    0, 0, 0, personality, list)

                saveUserInfo(user)

                /*val fragment = CreateProfileFragment2()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.create_profile_nav_container,fragment)?.commit()*/

            }
        }

    }

    private fun saveUserInfo(user: User) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        usersRef.child(currentUserID).setValue(user).addOnCompleteListener {

            if(it.isSuccessful)
            {
                addProfileImage()
                val intent = Intent(this, PersonalityTestActivity::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this, "Failed to store data", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun addProfileImage() {

        imageUri = Uri.parse("android.resource://${this.packageName}/${R.drawable.default_user}")
        storageReference = FirebaseStorage.getInstance().getReference("Users/" + FirebaseAuth.getInstance().currentUser!!.uid)
        storageReference.putFile(imageUri).addOnSuccessListener {

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

}