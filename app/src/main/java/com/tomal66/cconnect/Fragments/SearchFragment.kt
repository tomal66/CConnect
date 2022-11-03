package com.tomal66.cconnect.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tomal66.cconnect.Adapter.SearchAdapter
import com.tomal66.cconnect.Model.User
import com.tomal66.cconnect.R


class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView?= null
    private var searchAdapter: SearchAdapter?= null
    private var mUser: MutableList<User>?= null
    private var mAuth = FirebaseAuth.getInstance()

    @BindView(R.id.search_bar)
    lateinit var searchbar: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        ButterKnife.bind(this,view)

        recyclerView = view.findViewById(R.id.search_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        searchAdapter = context?.let { SearchAdapter(it, mUser as ArrayList<User>,true)}
        recyclerView?.adapter = searchAdapter

        searchbar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (searchbar.text.toString().trim()==""){
                    mUser?.clear()
                }
                else
                {
                    retrieveUsers()
                    searchUser(p0.toString().toLowerCase())
                }
            }
        })

        return view
    }

    private fun searchUser(input: String) {
        var query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullName")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mUser?.clear()

                for(snapshot in dataSnapshot.children)
                {
                    val user = snapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid!= user?.uid){
                        mUser?.add(user!!)
                    }
                }
                searchAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun retrieveUsers() {
        var usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(searchbar.text.toString().trim()=="")
                {
                    mUser?.clear()

                    for(snapshot in dataSnapshot.children)
                    {
                        val user = snapshot.getValue(User::class.java)
                        if(user!=null)
                        {
                            mUser?.add(user)
                        }
                    }
                    searchAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}