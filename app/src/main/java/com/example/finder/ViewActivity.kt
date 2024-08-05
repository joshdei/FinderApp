package com.example.finder

import Uploadroomate
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finder.databinding.ActivityViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance().reference

        val roommate = intent.getParcelableExtra<Uploadroomate>("ROOMMATE")
        if (roommate != null) {
            displayRoommateDetails(roommate)
            fetchUserDetails(roommate.userid)
        } else {
            // Handle the case where roommate is null
            Toast.makeText(this, "No roommate data found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val homeActivity = Intent(this, MainActivity::class.java)
                    startActivity(homeActivity)
                    true
                }
                R.id.navigation_setting -> {
                    val settingActivity = Intent(this, SettingActivity::class.java)
                    startActivity(settingActivity)
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
    }

    private fun displayRoommateDetails(roommate: Uploadroomate) {
        binding.title.text = roommate.title
        binding.numberOfGuest.text = roommate.numberOfGuest
        binding.address.text = roommate.address
        binding.pricePerNight.text = roommate.pricePerNight
        binding.availableDate.text = roommate.availableDate
        binding.date.text = roommate.date
        binding.description.text = roommate.description

        // Load image using Glide
        Glide.with(this)
            .load(roommate.imageUri)
            .placeholder(R.drawable.baseline_cloud_upload_24) // Add a placeholder image if needed
            .into(binding.image)
    }

    private fun fetchUserDetails(userId: String?) {
        if (userId == null) return
        database.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Users::class.java)
                if (user != null) {
                    binding.userName.text = user.fullname
                    binding.userphone.text = user.phone

                    // Make phone number clickable
                    binding.userphone.setOnClickListener {
                        openWhatsApp(user.phone)
                    }

                    // Make username clickable to view profile
                    binding.userName.setOnClickListener {
                        val intent = Intent(this@ViewActivity, ViewprofileActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@ViewActivity, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewActivity, "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openWhatsApp(phone: String) {
        val formattedPhone = phone.replace("[^0-9]".toRegex(), "")
        val uri = Uri.parse("https://wa.me/$formattedPhone")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}