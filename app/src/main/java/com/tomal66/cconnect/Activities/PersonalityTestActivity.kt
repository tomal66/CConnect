package com.tomal66.cconnect.Activities

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R

class PersonalityTestActivity : AppCompatActivity() {
    private  var i = 0;
    private var length = 0;
    private var currentProgress = 0;
    private lateinit var nextBtn:Button
    private lateinit var changingText : TextView
    private lateinit var changingButton1: Button
    private lateinit var changingButton2: Button
    private lateinit var user: User
    @BindView(R.id.quizProgress)
    lateinit var progressBar : ProgressBar
    lateinit var radioGroup : RadioGroup
    //val checkedRadioButtonId = radioGroup.checkedRadioButtonId
    private val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    var usable: Array<String> = arrayOf(
        "You regularly make new friends.",
        "You spend a lot of your free time exploring various random topics that pique your interest",
        "Seeing other people cry can easily make you feel like you want to cry too.",
        "You often make a backup plan for a backup plan.",
        "You usually stay calm, even under a lot of pressure.",
        "At social events, you rarely try to introduce yourself to new people and mostly talk to the ones you already know.",
        "You are more inclined to follow your head than your heart.",
        "You usually prefer just doing what you feel like at any given moment instead of planning a particular daily routine.",
        "You rarely worry about whether you make a good impression on people you meet.",
        "You enjoy participating in group activities.",
        "You like books and movies that make you come up with your own interpretation of the ending.",
        "You are interested in so many things that you find it difficult to choose what to try next.",
        "You are prone to worrying that things will take a turn for the worse.",
        "You avoid leadership roles in group settings",
        "You are definitely not an artistic type of person.",
        "You prefer to do your chores before allowing yourself to relax.",
        "You enjoy watching people argue.",
        "You tend to avoid drawing attention to yourself."

    );

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personality_test)
        ButterKnife.bind(this)
        changingText = findViewById(R.id.text_to_change);
        changingButton1 = findViewById(R.id.nextBtn);
        changingButton2 = findViewById(R.id.prevBtn);
        radioGroup = findViewById(R.id.radioGroup)
        progressBar.max = 18*100
        progressBar.progress = currentProgress

        usersRef.child(currentUserID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                user = snapshot.getValue(User::class.java)!!
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


       /* nextBtn = findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener(){
            val intent = Intent(this, InterestsActivity::class.java)
            startActivity(intent)
        }*/
        initializeQuiz()
        changeTextViewOnButtonClick();
        //changeTextOnce();
    }



    private fun changeTextViewOnButtonClick() {
        length = usable.size
        changingButton1.setOnClickListener()
        {

            i++;
            //changingText.text = usable[i];
            currentProgress = i*100
            ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
                .setDuration(200)
                .start()
            //progressBar.progress = currentProgress
            checkingStatus();

        };
        changingButton2.setOnClickListener(){

            i--;
            currentProgress = i*100
            //changingText.text = usable[i];
            ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
                .setDuration(200)
                .start()
            //progressBar.progress = currentProgress
            checkingStatus();
        }

    }

    private fun initializeQuiz()
    {
        changingText.text = usable[0]
    }

    private fun checkingStatus() {
        if(i>17)
        {
            FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).setValue(user)
            finish()
            val intent = Intent(this, InterestsActivity::class.java);
            startActivity(intent);
        }
        else if(i < 0)
        {

            finish()
        }
        else
        {
            setQuizVal()
            changingText.text = usable[i]
        }
    }

    private fun setQuizVal()
    {
        var checkedBtn = radioGroup.checkedRadioButtonId
        when(checkedBtn) {
            R.id.radio_button_1 -> {
                user.personality?.set(i,1)
                //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
            }
            R.id.radio_button_2 -> {
                user.personality?.set(i,2)
            }
            R.id.radio_button_3 -> {
                user.personality?.set(i,3)
            }
            R.id.radio_button_4 -> {
                user.personality?.set(i,4)
            }
            R.id.radio_button_5 -> {
                user.personality?.set(i,5)
            }
        }


        reset()
    }

    private fun reset()
    {
        radioGroup.check(R.id.radio_button_3)
    }


}


//i er value 0 thek 17 jabe
//i er value 0 theke kom hole previous activity te jabe , activity close korar function call korte hbe
//i er value 17 theke greater hole activity switch hobe, interest activity te switch hobe
//nextBtn er moton prevBtn initiali
//changingbutton becomes nextbtn
//creating a function jetar parameter i ar sheta i er value er upor base kore decide korbe ki korbe
//nextbtn er onclicklistener hobe i++ then function er moddhe i call