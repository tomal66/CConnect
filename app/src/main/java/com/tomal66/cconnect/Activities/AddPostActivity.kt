package com.tomal66.cconnect.Activities

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomal66.cconnect.Model.Post
import com.tomal66.cconnect.databinding.ActivityAddPostBinding
import com.yalantis.ucrop.UCrop
import java.io.*
import java.lang.ref.WeakReference
import java.util.*

class AddPostActivity : AppCompatActivity() {


    private lateinit var binding : ActivityAddPostBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var post :Post
    private var imageLink: String = "tomal"

    var picChanged = false

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    // Req code to get permission of camera and gallery
    private val GALLERY_REQUEST_CODE = 1234
    private val WRITE_EXTERNAL_STORAGE_CODE = 1

    // finalUri is tha final processed image after cropping
    lateinit var finalUri: Uri



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dialog = ProgressDialog(this@AddPostActivity)

        dialog.setCancelable(false)
        dialog.setTitle("Post uploading")
        dialog.setMessage("Please wait...")
        dialog.setCanceledOnTouchOutside(false)




        binding.close.setOnClickListener() {
            finish()
        }
        binding.addPost.setOnClickListener{

//            dialog.show()
            addPostFirebase()
//            dialog.dismiss()
            finish()
        }
        binding.useCamera.setOnClickListener{
            selectCamera()
        }
        binding.useGallery.setOnClickListener{
            selectGallery()
        }

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



    }

    private fun addPostFirebase() {

        if(currentUserID.isNotEmpty()) {
            val postRef = FirebaseDatabase.getInstance().getReference("Posts")

            if(binding.postTitle.toString().isNotEmpty() && binding.descriptionPost.toString().isNotEmpty())
            {
                var time  = Date().toString()
                val postID = postRef.push().key
                if(picChanged)
                {
                    storageReference = FirebaseStorage.getInstance().getReference().child("Posts")


                    storageReference.child(postID.toString()).putFile(finalUri).addOnSuccessListener {

                    }

                    post = Post(postID,binding.postTitle.text.toString(),binding.descriptionPost.text.toString(),finalUri.toString(),currentUserID,time)



                }
                else{

                    post = Post(postID,binding.postTitle.text.toString(),binding.descriptionPost.text.toString(),null ,currentUserID,time)

                }
                postRef.child(postID.toString()).setValue(post)

            }
            else{
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show()
            }

        }
        else{
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show()
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
//        saveMediaToStorage(bitmap)
//
    }
    private fun saveImage(image: Bitmap?, context: android.content.Context): Uri {



        var imageFolder= File(context.cacheDir,"images")
        var uri: Uri? = null

        try {

            imageFolder.mkdirs()
            var file: File = File(imageFolder,"captured_image.png")
            var stream: FileOutputStream = FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            uri= FileProvider.getUriForFile(context.applicationContext,"com.tomal66.cconnect"+".provider",file)


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }catch (e: IOException){
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
            val resultUri : Uri?= UCrop.getOutput(data!!)

            setImage(resultUri!!)

            picChanged = true;
            finalUri=resultUri
            saveEditedImage()

        }
    }
    private fun launchImageCrop(uri: Uri) {


        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options:UCrop.Options=UCrop.Options()


        options.setCompressionQuality(50)

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
            .into(binding.imagePost)
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