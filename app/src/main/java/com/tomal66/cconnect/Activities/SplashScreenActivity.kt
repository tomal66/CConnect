package com.tomal66.cconnect.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.tomal66.cconnect.R

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var logo : ImageView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        auth = Firebase.auth
        logo = findViewById(R.id.logo)
        logo.alpha = 0f
        logo.animate().setDuration(2000).alpha(1f).withEndAction {

            val currentUser = auth.currentUser


            if(currentUser != null  ){
                val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!.uid)

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.getValue()==null) {
                            val intent = Intent(this@SplashScreenActivity, CreateProfileActivity::class.java)

                            startActivity(intent)
                            finish()
                        }
                        else{
                            updateUI()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

            else{
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                startActivity(intent)
                finish()
            }

        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        // FireBase App check token for debugging
        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

    }


    private fun updateUI() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}