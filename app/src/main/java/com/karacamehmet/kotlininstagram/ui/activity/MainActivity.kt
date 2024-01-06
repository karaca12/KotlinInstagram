package com.karacamehmet.kotlininstagram.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.karacamehmet.kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }




        binding.buttonSignUp.setOnClickListener { v ->
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(
                    v,
                    "Email or password fields can't be empty!",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        //success
                        val intent = Intent(this, FeedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        //failed
                        Snackbar.make(
                            v,
                            it.localizedMessage as CharSequence,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        binding.buttonSignIn.setOnClickListener { v ->
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(
                    v,
                    "Email or password fields can't be empty!",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        //success
                        val intent = Intent(this, FeedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        //failed
                        Snackbar.make(
                            v,
                            it.localizedMessage as CharSequence,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            }
        }

    }

}