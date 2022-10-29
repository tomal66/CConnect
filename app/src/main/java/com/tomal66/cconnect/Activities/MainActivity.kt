package com.tomal66.cconnect.Activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import butterknife.ButterKnife
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mazenrashed.MenuBottomSheet
import com.tomal66.cconnect.*
import com.tomal66.cconnect.Fragments.*
import com.tomal66.cconnect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var binding : ActivityMainBinding
    val bottomSheet = MenuBottomSheet.Builder()
        .setMenuRes(R.menu.profile_options_menu)
        .closeAfterSelect(true)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButterKnife.bind(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        setupWithNavController(bottomNavigationView, navController)









        /*binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> 
                R.id.searchFragment ->
                R.id.notificationFragment ->
                R.id.profileFragment ->
                else -> {

                }

            }
            true
        }*/


    }

    private  fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_container,fragment)
        fragmentTransaction.commit()
    }

    override fun onStart() {
        super.onStart()

    }

    public fun showBottomSheet(){
        bottomSheet.show(this)
        bottomSheet.onSelectMenuItemListener = { position: Int, id: Int? ->
            when (id) {
                R.id.bottomsheet_settings -> replaceFragment(OptionsFragment())
                R.id.bottomsheet_report -> sendEmail("intesar3006@gmail.com", "Application bug found by user","")
                R.id.bottomsheet_logout -> logOut()
                else -> Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logOut(){
        Firebase.auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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