package com.tomal66.cconnect.Activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomal66.cconnect.Fragments.HomeFragment
import com.tomal66.cconnect.Fragments.ProfileFragment
import com.tomal66.cconnect.Model.User
//import com.rengwuxian.materialedittext.MaterialEditText
import com.tomal66.cconnect.R
import com.tomal66.cconnect.databinding.ActivityEditProfileBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var close : ImageView
    private lateinit var image_profile : ImageView
    private lateinit var save : ImageView
    private lateinit var tv_change : TextView
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var dialog: AlertDialog.Builder
    private lateinit var user : User
    private lateinit var storageReference: StorageReference

    @BindView(R.id.image_profile)
    lateinit var profileImage : ImageView

    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    private val GALLERY_REQUEST_CODE = 1234
    private val Write_External_Storage_Code = 1

    lateinit var finalUri: Uri



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showAllData()

        dialog = AlertDialog.Builder(this).setMessage("Updating Profile...")
            .setCancelable(false)

//        database = FirebaseDatabase.getInstance()
//
//        storage = FirebaseStorage.getInstance()

        binding.userImage.setOnClickListener {
            pickFromGallery()
        }

        binding.update.setOnClickListener{
            updateData()
        }

        binding.tvChange.setOnClickListener{
            pickFromGallery()
        }



        close = findViewById(R.id.close)
        close.setOnClickListener() {
            finish()
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun showAllData()
    {

        if(currentUserID.isNotEmpty()){
            usersRef.child(currentUserID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!

                    binding.editFirstName.setText(user.firstname )
                    binding.editLastName.setText( user.lastname )
                    binding.editAge.setText(user.age)
                    binding.editBio.setText(user.bio)
                    //binding.editGender.setSelection(3)
                    binding.editCountry.setText(user.country)
                    binding.editCity.setText(user.city)
                    binding.editInstitution.setText(user.institution)
                    binding.editDepartment.setText(user.department)

                    storageReference = FirebaseStorage.getInstance().reference.child("Users/$currentUserID")

                    val localFile = File.createTempFile("tempImage","jpg")

                    storageReference.getFile(localFile).addOnSuccessListener {

                        val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
                        binding.userImage.setImageBitmap(bitmap)


                    }.addOnFailureListener{
                        Toast.makeText(this@EditProfileActivity, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
                        //Toast.makeText(this,"Failed to retrieve image",Toast.LENGTH_SHORT).show()
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }





    }

    private fun updateData() {

        if(currentUserID.isNotEmpty()) {

            usersRef.child(currentUserID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    user = snapshot.getValue(User::class.java)!!


                    if (firstNameChanged() || lastNameChanged() || ageChanged() || bioChanged() || countryChanged() || cityChanged() || institutionChanged() || deptChanged() || GenderChanged()) {
                        val user1 = User(user.username,
                            binding.editFirstName.editableText.toString()+ " " + binding.editLastName.editableText.toString(),
                            binding.editFirstName.editableText.toString(),
                            binding.editLastName.editableText.toString(),
                            binding.editAge.editableText.toString(),
                            binding.editGender.selectedItem.toString(),
                            binding.editInstitution.editableText.toString(),
                            binding.editDepartment.editableText.toString(),
                            binding.editCity.editableText.toString(),
                            binding.editCountry.editableText.toString(),
                            binding.editBio.editableText.toString()
                        )


                        FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).setValue(user1).
                        addOnCompleteListener {

                            if(it.isSuccessful)
                            {
                                val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                                Toast.makeText(this@EditProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(this@EditProfileActivity, "Failed to update data",Toast.LENGTH_SHORT).show()
                            }
                            finish()

                        }

                    }
                    else {
                        Toast.makeText(this@EditProfileActivity, "Nothing to change", Toast.LENGTH_SHORT).show()
                    }

                    saveEditedImage()

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })

        }
    }

    private fun saveEditedImage() {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, finalUri)
        saveMediaToStorage(bitmap)

    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${currentUserID}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Photos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun GenderChanged(): Boolean {
        if(!user.gender.equals(binding.editGender.selectedItem.toString()))
        {
//            user.department = binding.editDepartment.editableText.toString()
            return true
        }
        return false
    }

    private fun deptChanged(): Boolean {
        if(!user.department.equals(binding.editDepartment.editableText.toString()))
        {
            return true
        }
        return false
    }

    private fun institutionChanged(): Boolean {
        if(!user.institution.equals(binding.editInstitution.editableText.toString()))
        {
            return true
        }
        return false
    }

    private fun cityChanged(): Boolean {
        if(!user.city.equals(binding.editCity.editableText.toString()))
        {
            return true
        }
        return false
    }

    private fun countryChanged(): Boolean {
        if(!user.country.equals(binding.editCountry.editableText.toString()))
        {
            return true
        }
        return false
    }

    private fun bioChanged(): Boolean {
        if(!user.bio.equals(binding.editBio.editableText.toString()))
        {
            return true
        }
        return false
    }

    private fun ageChanged(): Boolean {
        if(!user.age.equals(binding.editAge.editableText.toString()))
        {
            return true
        }
        return false
    }

    private fun lastNameChanged(): Boolean {
        if(!user.lastname.equals(binding.editLastName.editableText.toString()))
        {
            return true
        }
        return false
    }

    private fun firstNameChanged(): Boolean {
        if(!user.firstname.equals(binding.editFirstName.editableText.toString()))
        {
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }

                else{

                }
            }



        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri :Uri ?= UCrop.getOutput(data!!)

            setImage(resultUri!!)

            finalUri=resultUri


        }


    }


    private fun launchImageCrop(uri: Uri) {


        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options:UCrop.Options=UCrop.Options()


        UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(cacheDir,destination)))
            .withOptions(options)
            .withAspectRatio(0F, 0F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000)
            .start(this)


    }
    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(binding.userImage)
    }



}

