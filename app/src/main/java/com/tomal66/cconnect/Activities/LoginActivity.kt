package com.tomal66.cconnect.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tomal66.cconnect.R

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBtn : Button
    private lateinit var signUpBtn : TextView
    private lateinit var reportBtn : TextView
    private lateinit var editUsername : EditText
    private lateinit var editPassword : EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth
        loginBtn = findViewById(R.id.loginBtn)
        signUpBtn = findViewById(R.id.signUpBtn)
        reportBtn = findViewById(R.id.reportBtn)
        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)

        loginBtn.setOnClickListener(){

            val sEmail = editUsername.text.toString().trim()
            val sPassword = editPassword.text.toString().trim()
            if(sEmail.isEmpty() || sPassword.isEmpty())
            {
                Toast.makeText(baseContext, "Fields are empty!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.signInWithEmailAndPassword(sEmail, sPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            //updateUI()
                        }
                    }
            }

        }

        signUpBtn.setOnClickListener(){
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        reportBtn.setOnClickListener(){
            sendEmail("intesar3006@gmail.com", "Application bug found by user","")
        }
    }

    private fun updateUI() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI()
        }
    }




    public fun report(){

    }
    fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

    }

}