package com.example.finder

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.GrammaticalInflectionManagerCompat.GrammaticalGender
import com.example.finder.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var databaseReference: DatabaseReference
    private var userId: String? = null
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding.btnCreate.setOnClickListener {
            val fullname = binding.fullName.text.toString()
            val phone = binding.phoneNumber.text.toString()
            val gender = binding.gender.text.toString()
            val email = binding.email.text.toString()
            val rePassword = binding.rePassword.text.toString()
            val password = binding.password.text.toString()

            if (rePassword.isNotEmpty() && password.isNotEmpty()) {
                if (password == rePassword) {
                    if (fullname.isNotBlank() && phone.isNotBlank() && gender.isNotEmpty() && email.isNotBlank()) {
                        createAccount(fullname, phone, gender, email, password)
                    } else {
                        Toast.makeText(this, "Fill all input fields", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Password fields are empty", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val loginIntent = Intent(this, Login::class.java)
            startActivity(loginIntent)
            finish()
        }

        binding.btnForget.setOnClickListener {
            val forgetIntent = Intent(this, Forget::class.java)
            startActivity(forgetIntent)
            finish()
        }
    }

    private fun createAccount(fullname: String, phone: String, gender: String, email: String, password: String) {
        showProgressBar()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                userId = auth.currentUser?.uid
                userId?.let { uid ->
                    val user = Users(fullname, phone, gender, email, uid)
                    databaseReference.child(uid).setValue(user).addOnCompleteListener { userTask ->
                        if (userTask.isSuccessful) {
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_LONG).show()
                            val loginIntent = Intent(this, Login::class.java)
                            startActivity(loginIntent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to create account in database", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show()
            }
            hideProgressBar()
        }
    }

    private fun showProgressBar() {
        dialog = Dialog(this@Register)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }
}
