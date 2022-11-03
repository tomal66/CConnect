package com.tomal66.cconnect.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tomal66.cconnect.Fragments.ProfileFragment
import com.tomal66.cconnect.R

class OptionsActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var changePassword: RelativeLayout
    private lateinit var changeEmail: RelativeLayout
    private lateinit var visibility: RelativeLayout
    private lateinit var privacy: RelativeLayout
    private lateinit var report: RelativeLayout
    private lateinit var logout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        back = findViewById(R.id.back)
        changePassword = findViewById(R.id.change_password)
        changeEmail = findViewById(R.id.change_email)
        visibility = findViewById(R.id.visibility)
        privacy = findViewById(R.id.privacy)
        report = findViewById(R.id.report)
        logout = findViewById(R.id.logout)

        changeEmail.setOnClickListener(){
            changeEmailDialog()
        }

        changePassword.setOnClickListener(){
            changePasswordDialog()
        }

        back.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        logout.setOnClickListener(){
            Firebase.auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun changePasswordDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Change Email")
        val changeBtn: Button = mDialogView.findViewById(R.id.changeBtn)
        val cancelBtn: Button = mDialogView.findViewById(R.id.cancelBtn)
        val currPasswordET: EditText = mDialogView.findViewById(R.id.currPasswordET)
        val newPasswordET: EditText = mDialogView.findViewById(R.id.newPasswordET)
        val confPasswordET: EditText = mDialogView.findViewById(R.id.confPasswordET)

        val mAlertDialog = mBuilder.show()

        changeBtn.setOnClickListener(){
            mAlertDialog.dismiss()
            val currentPassword = currPasswordET.text.toString().trim()
            val newPassword = newPasswordET.text.toString().trim()
            val confirmPassword = confPasswordET.text.toString().trim()
            val user = Firebase.auth.currentUser

            if(newPassword.isEmpty() || currentPassword.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this,"Fields are empty!", Toast.LENGTH_SHORT).show()
            }
            else if(newPassword!=confirmPassword)
            {
                Toast.makeText(this,"Passwords don't match!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val credential = user?.email?.let { it1 ->
                    EmailAuthProvider
                        .getCredential(it1, currentPassword)
                }
                if (credential != null) {
                    user.reauthenticate(credential)
                        .addOnCompleteListener { task->
                            if(task.isSuccessful)
                            {
                                user!!.updatePassword(newPassword)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this,"Password Updated", Toast.LENGTH_SHORT).show()
                                        }
                                        else
                                        {
                                            Toast.makeText(this,"Error!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                            }
                            else
                            {
                                Toast.makeText(this,"Wrong Password!", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        cancelBtn.setOnClickListener(){
            mAlertDialog.dismiss()
        }
    }

    private fun changeEmailDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_email, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Change Email")
        val changeBtn: Button = mDialogView.findViewById(R.id.changeBtn)
        val cancelBtn: Button = mDialogView.findViewById(R.id.cancelBtn)
        val newEmailET: EditText = mDialogView.findViewById(R.id.newEmailET)
        val currPasswordET: EditText = mDialogView.findViewById(R.id.currPasswordET)

        val mAlertDialog = mBuilder.show()

        changeBtn.setOnClickListener(){
            mAlertDialog.dismiss()
            val newEmail = newEmailET.text.toString().trim()
            val currentPassword = currPasswordET.text.toString().trim()
            val user = Firebase.auth.currentUser

            if(newEmail.isEmpty() || currentPassword.isEmpty())
            {
                Toast.makeText(this,"Fields are empty!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val credential = user?.email?.let { it1 ->
                    EmailAuthProvider
                        .getCredential(it1, currentPassword)
                }
                if (credential != null) {
                    user.reauthenticate(credential)
                        .addOnCompleteListener { task->
                            if(task.isSuccessful)
                            {
                                user!!.updateEmail(newEmail)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this,"Email Updated", Toast.LENGTH_SHORT).show()
                                        }
                                        else
                                        {
                                            Toast.makeText(this,"Error!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                            }
                            else
                            {
                                Toast.makeText(this,"Wrong Password!", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        cancelBtn.setOnClickListener(){
            mAlertDialog.dismiss()
        }

    }
}