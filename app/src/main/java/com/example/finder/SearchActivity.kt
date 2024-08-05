package com.example.finder

import Uploadroomate
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var searchResults: ArrayList<Uploadroomate>
    private lateinit var searchRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchRecyclerView = findViewById(R.id.rv_searchResults)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.setHasFixedSize(true)
        searchResults = arrayListOf()
        searchRecyclerView.adapter = RecyclerAdapter(searchResults)

        val query = intent.getStringExtra("SEARCH_QUERY")
        if (query != null) {
            searchRoommates(query)
        }
    }

    private fun searchRoommates(query: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploadroomate")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    searchResults.clear()
                    for (userSnapshot in snapshot.children) {
                        val roommate = userSnapshot.getValue(Uploadroomate::class.java)
                        if (roommate != null && roommate.title.contains(query, true)) {
                            searchResults.add(roommate)
                        }
                    }
                    (searchRecyclerView.adapter as RecyclerAdapter).notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SearchActivity", "Error fetching search results", error.toException())
            }
        })
    }
}
