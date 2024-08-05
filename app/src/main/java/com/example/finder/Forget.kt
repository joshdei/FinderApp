package com.example.finder

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finder.databinding.ActivityForgetBinding
import com.example.finder.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Forget : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgetBinding
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        binding.btnForget.setOnClickListener {
            val email = binding.userEmail.text.toString()
            if (email.isNotBlank()) {
                forgetPassword(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val loginIntent = Intent(this, Login::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    private fun forgetPassword(email: String) {
        showProgressBar()
        auth.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Check your email to reset your password", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_LONG).show()
            }
            hideProgressBar()
        }
    }

    private fun showProgressBar() {
        dialog = Dialog(this@Forget)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }
}