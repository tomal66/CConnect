package com.tomal66.cconnect.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.tomal66.cconnect.Activities.AddPeopleActivity
import com.tomal66.cconnect.Activities.ChatActivity
import com.tomal66.cconnect.Activities.EditProfileActivity
import com.tomal66.cconnect.Adapter.PostAdapter
import com.tomal66.cconnect.Model.Post
import com.tomal66.cconnect.R
import com.tomal66.cconnect.databinding.ActivityAddPostBinding
import com.tomal66.cconnect.databinding.PostItemBinding


class HomeFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private lateinit var binding: PostItemBinding
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")
    lateinit var finalUri: Uri
    private lateinit var auth : FirebaseAuth
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList : MutableList<Post>? = null

    companion object val TAG = "aaa"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val messages:ImageView = view.findViewById(R.id.messages)


        messages.setOnClickListener(){
            val intent = Intent(activity, ChatActivity::class.java)
            startActivity(intent)
        }

        // Recycler View
        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.postRecyclerView)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it,postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        checkFollowings()


        val addPeople:ImageView = view.findViewById(R.id.add_people)
        addPeople.setOnClickListener(){
            val intent = Intent(activity, AddPeopleActivity::class.java)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {

                    (followingList as ArrayList<String>).clear()

                    for (po in snapshot.children)
                    {
                        Log.d(TAG,po.key!!)
                        po.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()

                for (po in snapshot.children)
                {
                    val post = po.getValue(Post:: class.java)

                    for (userID in (followingList as ArrayList<String>))
                    {
                        if(post!!.postedBy.equals(userID))
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}