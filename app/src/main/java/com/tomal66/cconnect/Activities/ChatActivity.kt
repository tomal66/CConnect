package com.tomal66.cconnect.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.disklrucache.DiskLruCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.tomal66.cconnect.Activities.ChatLogActivity.Companion.TAG
import com.tomal66.cconnect.Adapter.NewMessageAdapter
import com.tomal66.cconnect.Model.ChatMessage
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.io.File

class ChatActivity : AppCompatActivity() {

    companion object{
        val TAG = "LatestMessages"
    }

    private lateinit var back:ImageView
    private lateinit var new_chat:ImageView
    private lateinit var recyclerview_latest_messages:RecyclerView
    private lateinit var message_textview_latest_message:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerview_latest_messages = findViewById(R.id.recyclerview_latest_messages)
        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        //set item click listener on your adapter
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra("name", row.user?.firstname + " " + row.user?.lastname)
            intent.putExtra("uid", row.user?.uid)
            startActivity(intent)
        }

        back = findViewById(R.id.back)
        back.setOnClickListener(){
            finish()
        }
        new_chat = findViewById(R.id.new_chat)
        new_chat.setOnClickListener(){
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        //setupDummyRows()

        listenForLatestMessages()
    }


    class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>() {

        var user: User? = null
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val message_textview_latest_message: TextView = viewHolder.itemView.findViewById(R.id.message_textview_latest_message)
            //val username_textview_latest_message: TextView = viewHolder.itemView.findViewById(R.id.username_textview_latest_message)
            message_textview_latest_message.text = chatMessage.text

            val chatPartnerId: String
            if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatPartnerId = chatMessage.toId
            }else{
                chatPartnerId = chatMessage.fromId
            }

            val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
            Log.d(TAG, chatPartnerId)
            usersRef.child(chatPartnerId).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!
                    val username : TextView = viewHolder.itemView.findViewById(R.id.username_textview_latest_message)
                    username.text = user!!.firstname + " " + user!!.lastname
                    val storageReference = FirebaseStorage.getInstance().reference.child("Users/${user!!.uid}")

                    val localFile = File.createTempFile("tempImage","jpg")

                    storageReference.getFile(localFile).addOnSuccessListener {

                        val bitmap = BitmapFactory.decodeFile((localFile.absolutePath))
                        val profileImage :ImageView = viewHolder.itemView.findViewById(R.id.imageView4)
                        profileImage.setImageBitmap(bitmap)


                    }.addOnFailureListener{
                        //push karo khush raho
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }
    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        //recyclerview_latest_messages = findViewById(R.id.recyclerview_latest_messages)
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    //private fun setupDummyRows(){
        //val adapter = GroupAdapter<GroupieViewHolder>()

      //  adapter.add(LatestMessageRow())
        //adapter.add(LatestMessageRow())
        //adapter.add(LatestMessageRow())

        //recyclerview_latest_messages.adapter = adapter
    //}
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Basically this is kind of a switch statement
        when(item?.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}