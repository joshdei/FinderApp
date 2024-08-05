package com.example.finder

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finder.databinding.ActivityHostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var userid: String? = null
    private var selectedItem: String? = null
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        userid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("HostRegistration")

        val spinner: Spinner = binding.mySpinner

        // Define the items
        val items = arrayOf("Student", "Landlord", "Caretaker", "Tenant")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Set a listener to perform actions when an item is selected
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Handle item selection
                selectedItem = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case where no item is selected, if needed
            }
        }

        if (userid != null) {
            binding.btnCreate.setOnClickListener {
                val nin = binding.nin.text.toString()
                if (selectedItem != null) {
                    createHost(nin, selectedItem!!, userid!!)
                } else {
                    Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createHost(nin: String, status: String, userid: String) {
        showProgressBar()
        val hostData = hashMapOf(
            "nin" to nin,
            "status" to status
        )
        databaseReference.child(userid).setValue(hostData).addOnCompleteListener(this) { task ->
            hideProgressBar()
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Creation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar() {
        dialog = Dialog(this@HostActivity)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }
}