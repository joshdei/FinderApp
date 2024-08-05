package com.example.finder

import Uploadroomate
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.SupportActionModeWrapper
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finder.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var roommateList: ArrayList<Uploadroomate>
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploadroomate")

        userRecyclerView = findViewById(R.id.rv_recyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)
        roommateList = arrayListOf()
        userRecyclerView.adapter = RecyclerAdapter(roommateList)

        getRoommateList()

        val searchEditText: EditText = findViewById(R.id.usersearch)
        binding.btnUpload.setOnClickListener {
            val uploadActivity = Intent(this, UploadActivity::class.java)
            startActivity(uploadActivity)
        }

        drawerLayout = findViewById(R.id.main)
        navView = findViewById(R.id.drawlayout)
        val menuButton: ImageButton = findViewById(R.id.menuButton)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }


        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    val homeActivity = Intent(this, MainActivity::class.java)
                    startActivity(homeActivity)
                    true
                }
                R.id.navigation_profile -> {
                    val homeActivity = Intent(this, ProfileActivity::class.java)
                    startActivity(homeActivity)
                    true
                }




            }
            true
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val homeActivity = Intent(this, MainActivity::class.java)
                    startActivity(homeActivity)
                    true
                }
                R.id.navigation_setting -> {
                    Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_profile -> {
                    val profileActivity = Intent(this, ProfileActivity::class.java)
                    startActivity(profileActivity)
                    true
                }
                else -> false
            }
        }

        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString()
                val searchIntent = Intent(this, SearchActivity::class.java)
                searchIntent.putExtra("SEARCH_QUERY", query)
                startActivity(searchIntent)
                true
            } else {
                false
            }
        }
    }

    private fun getRoommateList() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    roommateList.clear()
                    for (userSnapshot in snapshot.children) {
                        val roommate = userSnapshot.getValue(Uploadroomate::class.java)
                        roommate?.let { roommateList.add(it) }
                    }
                    (userRecyclerView.adapter as RecyclerAdapter).notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Error fetching data", error.toException())
            }
        })
    }
}