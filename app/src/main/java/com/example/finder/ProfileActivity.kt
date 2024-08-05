package com.example.finder

import Uploadroomate
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.finder.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dpReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var dialog: Dialog
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        dpReference = FirebaseDatabase.getInstance().getReference("ProfileDp")
        storageReference = FirebaseStorage.getInstance().reference

        userId = auth.currentUser?.uid.orEmpty()
        if (userId.isNotEmpty()) {
            getUserInfo()
            getUserDp()
            getFilteredUploadroomateList(userId)
        }

        binding.imageView.setOnClickListener {
            selectImage()
        }

        binding.btnuploaddp.setOnClickListener {
            uploadImage(imageUri, userId)
        }

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

    private fun getUserDp() {
        dpReference.child(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    val profilepic = snapshot.getValue(Profilepic::class.java)
                    profilepic?.profilepicurl?.let {
                        Glide.with(this)
                            .load(it)
                            .into(binding.imageView)
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("ProfileActivity", "Error fetching user info", task.exception)
                Toast.makeText(this@ProfileActivity, "Error fetching user info", Toast.LENGTH_SHORT).show()
            }
            hideProgressBar()
        }
    }

    private fun uploadImage(imageUri: Uri?, userId: String) {
        if (imageUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val fileName = formatter.format(now)
            val imageRef = storageReference.child("profiledp/$fileName")

            imageRef.putFile(imageUri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val profilepic = Profilepic(userId, imageUrl)
                    dpReference.child(userId).setValue(profilepic).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile picture uploaded successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_LONG).show()
                        }
                        progressDialog.dismiss()
                    }
                }.addOnFailureListener { e ->
                    Log.e("ProfileActivity", "Failed to get download URL", e)
                    Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }.addOnFailureListener { e ->
                Log.e("ProfileActivity", "Failed to upload image", e)
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.imageView.setImageURI(imageUri)
        }
    }

    private fun getUserInfo() {
        showProgressBar()
        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    user?.let {
                        binding.textViewfirstname.text = it.fullname
                        binding.lastName.text = it.gender
                        binding.textViewEmail.text = it.email
                        binding.tel.text = it.phone
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
                hideProgressBar()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Error fetching user info", error.toException())
                Toast.makeText(this@ProfileActivity, "Error fetching user info", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
        })
    }

    private fun showProgressBar() {
        dialog = Dialog(this@ProfileActivity)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }

    private fun getFilteredUploadroomateList(userId: String) {
        val roomateReference = FirebaseDatabase.getInstance().getReference("Uploadroomate")
        roomateReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredList = mutableListOf<Uploadroomate>()
                for (roomateSnapshot in snapshot.children) {
                    val roomate = roomateSnapshot.getValue(Uploadroomate::class.java)
                    if (roomate?.userid == userId) {
                        filteredList.add(roomate)
                    }
                }
                setupRecyclerView(filteredList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Error fetching roomate list", error.toException())
            }
        })
    }

    private fun setupRecyclerView(roomateList: List<Uploadroomate>) {
        val adapter = ViewprofileAdapter(roomateList)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter
    }
}