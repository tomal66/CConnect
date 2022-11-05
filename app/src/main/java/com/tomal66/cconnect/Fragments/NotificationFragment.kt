package com.tomal66.cconnect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tomal66.cconnect.Adapter.NotificationAdapter
import com.tomal66.cconnect.Adapter.PostAdapter
import com.tomal66.cconnect.Model.Notification
import com.tomal66.cconnect.Model.Post
import com.tomal66.cconnect.R


class NotificationFragment : Fragment() {


    private var notificationAdapter: NotificationAdapter? = null
    private var notificationList: MutableList<Notification>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_notification, container, false)


        // Recycler View
        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recyclerViewNotification)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        notificationList = ArrayList()
        notificationAdapter = context?.let { NotificationAdapter(it,notificationList as ArrayList<Notification>) }
        recyclerView.adapter = notificationAdapter

        checkNotification()

        return view
    }

    private fun checkNotification() {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val notificationRef = FirebaseDatabase.getInstance().reference.child("Notification").child(currentUserID)

        notificationRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList?.clear()

                for (noti in snapshot.children)
                {
                    val noti1 = noti.getValue(Notification::class.java)

                    noti1?.let { notificationList?.add(it) }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}