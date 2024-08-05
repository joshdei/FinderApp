package com.example.finder


import Uploadroomate
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finder.databinding.ActivityUploadBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var userId: String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid.orEmpty()
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploadroomate")
        storageReference = FirebaseStorage.getInstance().reference.child("roommate_pictures")

        val formatter = SimpleDateFormat("HH_mm", Locale.getDefault())
        val now = Date()
        date = formatter.format(now)

        binding.uploadimage.setOnClickListener {
            selectImage()
        }

        binding.availableDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnUploadRoom.setOnClickListener {
            val title = binding.title.text.toString()
            val numberOfGuest = binding.numberOfGuest.text.toString()
            val address = binding.address.text.toString()
            val pricePerNight = binding.pricePerNight.text.toString()
            val availableDate = binding.availableDate.text.toString()
            val description = binding.description.text.toString()
            uploadImage(userId, title, numberOfGuest, address, pricePerNight, availableDate, date, description, imageUri)
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.availableDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun uploadImage(
        userid: String,
        title: String,
        numberOfGuest: String,
        address: String,
        pricePerNight: String,
        availableDate: String,
        date: String,
        description: String,
        imageUri: Uri?
    ) {
        if (imageUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val fileName = formatter.format(now)
            val imageRef = storageReference.child("images/$fileName")

            // Compress the image before uploading
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) // 50% quality to reduce size
            val data = baos.toByteArray()

            imageRef.putBytes(data).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUriString = uri.toString()
                    val uploadRoommate = Uploadroomate(
                        userid, title, numberOfGuest, address, pricePerNight, availableDate, date, description, imageUriString
                    )
                    val newPostKey = databaseReference.push().key // Generate a new unique key
                    if (newPostKey != null) {
                        databaseReference.child(newPostKey).setValue(uploadRoommate).addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                Toast.makeText(this, "Roommate information uploaded successfully", Toast.LENGTH_LONG).show()
                                val homeActivity = Intent(this, MainActivity::class.java)
                                startActivity(homeActivity)
                            } else {
                                Toast.makeText(this, "Failed to upload roommate information", Toast.LENGTH_LONG).show()
                            }
                            if (progressDialog.isShowing) progressDialog.dismiss()
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.e("UploadActivity", "Failed to get download URL", e)
                    Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_LONG).show()
                    if (progressDialog.isShowing) progressDialog.dismiss()
                }
            }.addOnFailureListener { e ->
                Log.e("UploadActivity", "Failed to upload image", e)
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_LONG).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
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
            binding.uploadimage.setImageURI(imageUri)
        }
    }
}