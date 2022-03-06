package com.forumber.tokencasestudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.forumber.tokencasestudy.databinding.ActivityLoginBinding
import com.forumber.tokencasestudy.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var actionBar: ActionBar
    //private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Register"

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener {
            firebaseAuth.createUserWithEmailAndPassword(binding.inputEmail.text.toString(), binding.inputPassword.text.toString())
                .addOnSuccessListener {
                    Database.registerUser(binding.inputEmail.text.toString())
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}