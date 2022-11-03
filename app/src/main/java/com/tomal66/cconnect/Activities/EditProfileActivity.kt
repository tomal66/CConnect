package com.tomal66.cconnect.Activities

import android.Manifest
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import butterknife.BindView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mazenrashed.MenuBottomSheet
import com.tomal66.cconnect.Model.User
//import com.rengwuxian.materialedittext.MaterialEditText
import com.tomal66.cconnect.R
import com.tomal66.cconnect.databinding.ActivityEditProfileBinding
import com.yalantis.ucrop.UCrop
import java.io.*
import java.lang.ref.WeakReference
import java.util.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat


class EditProfileActivity : AppCompatActivity() {
    private lateinit var close : ImageView
    private lateinit var image_profile : ImageView
    private lateinit var save : ImageView
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var user : User
    private lateinit var storageReference: StorageReference

    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>

    @BindView(R.id.image_profile)
    lateinit var profileImage : ImageView
    var picChanged = false


    //database Ref
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    // Req code to get permission of camera and gallery
    private val GALLERY_REQUEST_CODE = 1234
    private val WRITE_EXTERNAL_STORAGE_CODE = 1

    // finalUri is tha final processed image after cropping
    lateinit var finalUri: Uri

    val bottomSheet = MenuBottomSheet.Builder()
        .setMenuRes(R.menu.change_dp_menu)
        .closeAfterSelect(true)
        .build()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showAllData()

        val dialog = ProgressDialog(this@EditProfileActivity)

        dialog.setCancelable(false)
        dialog.setTitle("Profile updating")
        dialog.setMessage("Please wait...")
        dialog.setCanceledOnTouchOutside(false)


        val c = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener(){ view, year, month, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "MM/dd/yy"
            val sdf = SimpleDateFormat(format, Locale.US)
            binding.editDOB.setText(sdf.format(c.time))
        }

        binding.editDOB.setOnClickListener(){
            DatePickerDialog(this, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }


        checkPermission()
        requestPermission()


        activityResultLauncher  =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                if (result.resultCode== RESULT_OK) {

                    var extras: Bundle? = result.data?.extras

                    var imageUri: Uri

                    var imageBitmap = extras?.get("data") as Bitmap

                    var imageResult: WeakReference<Bitmap> = WeakReference(
                        Bitmap.createScaledBitmap(
                            imageBitmap, imageBitmap.width, imageBitmap.height, false
                        ).copy(
                            Bitmap.Config.RGB_565, true
                        )
                    )

                    var bm = imageResult.get()

                    imageUri = saveImage(bm, this)


                    launchImageCrop(imageUri)

                }

                else{

                }

            }

        binding.update.setOnClickListener{
            dialog.show()
            updateData()
//            dialog.dismiss()
            finish()
        }
        binding.changePictureButton.setOnClickListener{
            showBottomSheet()
        }

        close = findViewById(R.id.close)
        close.setOnClickListener() {
            finish()
        }
    }

    fun showBottomSheet(){
        bottomSheet.show(this)
        bottomSheet.onSelectMenuItemListener = { position: Int, id: Int? ->
            when (id) {
                R.id.bottomsheet_camera -> selectCamera()
                R.id.bottomsheet_gallery -> selectGallery()
                else -> Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun selectCamera()
    {
        if(checkPermission()) {
            pickFromCamera()
        }
        else{
            Toast.makeText(this, "Allow all permissions", Toast.LENGTH_SHORT).show()
            requestPermission()
        }
    }
    fun selectGallery(){
        if(checkPermission()) {
            pickFromGallery()
        }
        else{
            Toast.makeText(this, "Allow all permissions", Toast.LENGTH_SHORT).show()
            requestPermission()
        }
    }

    private fun showAllData()
    {

        if(currentUserID.isNotEmpty()){
            usersRef.child(currentUserID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!

                    binding.editFirstName.setText(user.firstname )
                    binding.editLastName.setText( user.lastname )
                    binding.editDOB.setText(user.dob)
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


                    if (firstNameChanged() || lastNameChanged() || DOBChanged() || bioChanged() || countryChanged() || cityChanged() || institutionChanged() || deptChanged() || GenderChanged() || picChanged) {
                        val user1 = User(user.username,
                            (binding.editFirstName.editableText.toString()+ " " + binding.editLastName.editableText.toString()).toLowerCase(),
                            binding.editFirstName.editableText.toString(),
                            binding.editLastName.editableText.toString(),
                            binding.editDOB.editableText.toString(),
                            binding.editGender.selectedItem.toString(),
                            binding.editInstitution.editableText.toString(),
                            binding.editDepartment.editableText.toString(),
                            binding.editCity.editableText.toString(),
                            binding.editCountry.editableText.toString(),
                            binding.editBio.editableText.toString(),
                            user.uid,
                            user.posts,
                            user.followers,
                            user.following,
                            user.personality
                        )


                        FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).setValue(user1).
                        addOnCompleteListener {

                            if(it.isSuccessful)
                            {
                                val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
//                                Toast.makeText(this@EditProfileActivity, "Profile upfdhdated", Toast.LENGTH_SHORT).show()
                                finish()
                                //startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(this@EditProfileActivity, "Failed to update data",Toast.LENGTH_SHORT).show()
                            }

                        }

                    }

                    //saveEditedImage()
                    if(picChanged)
                    {
                        addProfileImage()
                    }
                    //finish()

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }
        else{
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show()
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

    private fun pickFromCamera(){

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncher.launch(intent)
    }



    private fun saveEditedImage() {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, finalUri)
        saveMediaToStorage(bitmap)

    }
    private fun saveImage(image: Bitmap?, context: android.content.Context): Uri {



        var imageFolder=File(context.cacheDir,"images")
        var uri: Uri? = null

        try {

            imageFolder.mkdirs()
            var file:File= File(imageFolder,"captured_image.png")
            var stream:FileOutputStream= FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            uri=FileProvider.getUriForFile(context.applicationContext,"com.tomal66.cconnect"+".provider",file)


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }

        return uri!!

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

            picChanged = true;
            finalUri=resultUri
            saveEditedImage()

        }
    }

    private fun addProfileImage() {

        storageReference = FirebaseStorage.getInstance().getReference("Users/" + FirebaseAuth.getInstance().currentUser!!.uid)
        storageReference.putFile(finalUri).addOnSuccessListener {
//            Toast.makeText(this, "Profile Updadggted", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image",Toast.LENGTH_SHORT).show()
        }
    }


    private fun launchImageCrop(uri: Uri) {


        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options:UCrop.Options=UCrop.Options()

        //image Compression

        options.setCompressionQuality(100);
        options.setMaxBitmapSize(1000);



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


    private fun GenderChanged(): Boolean {
        if(!user.gender.equals(binding.editGender.selectedItem.toString()))
        {
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

    private fun DOBChanged(): Boolean {
        if(!user.dob.equals(binding.editDOB.editableText.toString()))
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

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            100
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            WRITE_EXTERNAL_STORAGE_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "Enable permissions", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


}

