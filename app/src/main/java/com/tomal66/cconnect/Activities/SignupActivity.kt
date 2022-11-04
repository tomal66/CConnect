package com.tomal66.cconnect.Activities

import android.app.ProgressDialog
import android.content.Intent
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

class SignupActivity : AppCompatActivity() {
    private lateinit var nextBtn : Button
    private lateinit var signInBtn : TextView
    private lateinit var editUsername : EditText
    private lateinit var editPassword : EditText
    private lateinit var editConfirmPassword : EditText

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val dialog = ProgressDialog(this@SignupActivity)

        dialog.setCancelable(false)
        dialog.setTitle("Signing up")
        dialog.setMessage("Please wait...")
        dialog.setCanceledOnTouchOutside(false)


        // Initialize Firebase Auth
        auth = Firebase.auth
        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)


        nextBtn = findViewById(R.id.nextBtn)
        signInBtn = findViewById(R.id.signInBtn)

        nextBtn.setOnClickListener(){
            dialog.show()
            if(editPassword.text.toString()==editConfirmPassword.text.toString())
            {
                val sEmail = editUsername.text.toString().trim()
                val sPassword = editPassword.text.toString().trim()

                auth.createUserWithEmailAndPassword(sEmail, sPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser

                            dialog.dismiss()
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            dialog.dismiss()
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
//                            updateUI(null)
                        }
                    }
            }
            else {
                dialog.dismiss()
                Toast.makeText(baseContext, "Passwords don't match.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        signInBtn.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, CreateProfileActivity::class.java)
        startActivity(intent)
    }
}