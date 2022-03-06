package com.forumber.tokencasestudy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.forumber.tokencasestudy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var actionBar: ActionBar
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Login"

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.buttonRegister.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.buttonLogin.setOnClickListener {
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        firebaseAuth.signInWithEmailAndPassword(binding.inputEmail.text.toString(), binding.inputPassword.text.toString())
            .addOnSuccessListener {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        val loggedInUser = firebaseAuth.currentUser
        if (loggedInUser != null)
        {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}