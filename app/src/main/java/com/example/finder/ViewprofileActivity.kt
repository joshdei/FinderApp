package com.example.finder

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
import com.bumptech.glide.Glide
import com.example.finder.databinding.ActivityViewBinding
import com.example.finder.databinding.ActivityViewprofileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class ViewprofileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewprofileBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        val userId = intent.getStringExtra("USER_ID")
        if (userId != null) {
            fetchUserDetails(userId)
        } else {
            Toast.makeText(this, "No user ID provided", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set up bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.navigation_setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchUserDetails(userId: String) {
        database.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Users::class.java)
                if (user != null) {
                    // Update UI with user details
                    binding.textViewfirstname.text = user.fullname
                    binding.lastName.text = user.gender
                    binding.textViewEmail.text = user.email
                    binding.tel.text = user.phone

                    // Set up WhatsApp click listener
                    binding.tel.setOnClickListener {
                        openWhatsApp(user.phone)
                    }

                    // Fetch and display the user's profile picture
                    fetchUserProfilePicture(userId)
                } else {
                    Toast.makeText(this@ViewprofileActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ViewprofileActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserProfilePicture(userId: String) {
        database.child("ProfileDp").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val profilePic = dataSnapshot.getValue(Profilepic::class.java)
                if (profilePic != null) {
                    val imageUrl = profilePic.profilepicurl
                    Glide.with(this@ViewprofileActivity)
                        .load(imageUrl)
                        .placeholder(R.drawable.baseline_cloud_upload_24) // Optional placeholder
                        .error(R.drawable.baseline_cloud_upload_24) // Optional error image
                        .into(binding.imageView)
                } else {
                    Toast.makeText(this@ViewprofileActivity, "Profile picture not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ViewprofileActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
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
