package com.tomal66.cconnect.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tomal66.cconnect.R

class ForgotPasswordActivity : AppCompatActivity() {
    @BindView(R.id.resetEmail)
    lateinit var resetEmail : EditText
    @BindView(R.id.resetBtn)
    lateinit var resetBtn : Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        ButterKnife.bind(this)
        auth = Firebase.auth

        resetBtn.setOnClickListener(){
            val email = resetEmail.text.toString().trim()
            if(email.isEmpty())
            {
                Toast.makeText(baseContext, "Field is empty!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.sendPasswordResetEmail(email).addOnCompleteListener(this){ task ->

                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Check Email", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(baseContext, "Wrong Email!", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
    }
}