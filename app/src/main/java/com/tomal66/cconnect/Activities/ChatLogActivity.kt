package com.tomal66.cconnect.Activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tomal66.cconnect.Adapter.NewMessageAdapter
import com.tomal66.cconnect.Model.ChatMessage
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import com.tomal66.cconnect.databinding.ActivityEditProfileBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import org.w3c.dom.Text
import java.io.File

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    @BindView(R.id.fullName)
    lateinit var fullName: TextView

    private lateinit var recyclerview_chat_log : RecyclerView
    private lateinit var send_button_chat_log : ImageView
    private lateinit var edittext_chat_log : EditText
    private lateinit var backBtn: ImageView

    //    private lateinit var textview_from_row : Text
//    private lateinit var textview_to_row : Text
    private lateinit var toId : String
    private lateinit var fromId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        ButterKnife.bind(this)

        backBtn = findViewById(R.id.back)

        fromId = FirebaseAuth.getInstance().uid!!

        val name = intent.getStringExtra("name")
        fullName.text = name
        toId = intent.getStringExtra("uid").toString()

        edittext_chat_log = findViewById(R.id.edittext_chat_log)
        send_button_chat_log = findViewById(R.id.send_button_chat_log)
        recyclerview_chat_log = findViewById(R.id.recyclerview_chat_log)
        recyclerview_chat_log.adapter = adapter
//       textview_from_row = findViewById(R.id.textView2)
//       textview_to_row = findViewById(R.id.textView3)
        //setupDummyData()
        listenForMessages()

        send_button_chat_log.setOnClickListener{
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()
        }

        backBtn.setOnClickListener(){
            onBackPressed()
        }
    }


    private fun performSendMessage(){
        //how do we actually send a message to firebase

        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid

        val toId = toId

        if(fromId == null) return
        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toreference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis())
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear() // This will clear out the text after sending the msg
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1) // This will take you to the last position after sending msg. Won't need to scroll more
            }

        toreference.setValue(chatMessage)
            .addOnSuccessListener { Log.d(TAG, "Wrote: ${toreference.key}") }
            .addOnFailureListener{Log.d(TAG, "Failed to write")}

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "LatestMessageWorks")
            }


        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "LatestMessageToWorks")
            }
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                chatMessage?.text?.let { Log.d(TAG, it) }

                if(chatMessage!!.fromId == FirebaseAuth.getInstance().uid){
                    if (chatMessage != null) {
                        adapter.add(ChatToItem(chatMessage.text,))
                    }
                }else{
                    if (chatMessage != null) {
                        adapter.add(ChatFromItem(chatMessage.text,toId))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }
    private fun setupDummyData(){
        val adapter = GroupAdapter<GroupieViewHolder>()

//        adapter.add(ChatFromItem("Country roads"))
////        adapter.add(ChatToItem("Take me homeee",))
//        adapter.add(ChatFromItem("To the place"))
//        adapter.add(ChatToItem("I belooooong"))
        //adapter.add(ChatFromItem())
        //adapter.add(ChatToItem())
        //adapter.add(ChatFromItem())
        //adapter.add(ChatToItem())

        recyclerview_chat_log?.adapter = adapter
    }

}

class ChatFromItem(val text:String, val toId: String): Item<GroupieViewHolder>() {

    private lateinit var storageReference: StorageReference
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textview_from_row: TextView = viewHolder.itemView.findViewById(R.id.textView2)
        val imageview_chat_from_row : ImageView = viewHolder.itemView.findViewById(R.id.imageView2)
        textview_from_row.text = text

        //load our user image into the star
        storageReference = FirebaseStorage.getInstance().reference.child("Users/$toId")

        val localFile = File.createTempFile("tempImage","jpg")

        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
            imageview_chat_from_row.setImageBitmap(bitmap)


        }.addOnFailureListener{
//            Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
            //Toast.makeText(this,"Failed to retrieve image",Toast.LENGTH_SHORT).show()
        }
        val targetImageView = imageview_chat_from_row

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String): Item<GroupieViewHolder>() {

    private lateinit var storageReference: StorageReference
//    private lateinit var binding : ActivityEditProfileBinding
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")



    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textview_to_row: TextView = viewHolder.itemView.findViewById(R.id.textView3)
        val imageview_chat_to_row : ImageView = viewHolder.itemView.findViewById(R.id.imageView3)
        textview_to_row.text = text

        //load our user image into the star
        storageReference = FirebaseStorage.getInstance().reference.child("Users/$currentUserID")

        val localFile = File.createTempFile("tempImage","jpg")

        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
            imageview_chat_to_row.setImageBitmap(bitmap)


        }.addOnFailureListener{
//            Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
            //Toast.makeText(this,"Failed to retrieve image",Toast.LENGTH_SHORT).show()
        }
       val targetImageView = imageview_chat_to_row

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}