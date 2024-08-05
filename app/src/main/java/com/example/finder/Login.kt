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
import com.example.finder.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        binding.btnLogin.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val password = binding.usePass.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {

                login(email, password)
            } else {
                Toast.makeText(this, "Fill all input fields", Toast.LENGTH_LONG).show()
            }
        }
        binding.btnCreate.setOnClickListener {
            val registerIntent = Intent(this, Register::class.java)
            startActivity(registerIntent)
            finish()
        }

        binding.btnForget.setOnClickListener {
            val forgetIntent = Intent(this, Forget::class.java)
            startActivity(forgetIntent)
            finish()
        }
    }

    private fun login(email: String, password: String) {
        showProgressBar()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            } else {
                Toast.makeText(this, "Information is not correct", Toast.LENGTH_LONG).show()
            }
            hideProgressBar()
        }
    }

    private fun showProgressBar() {
        dialog = Dialog(this@Login)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }
}