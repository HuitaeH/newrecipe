package com.example.recipee  // This should match the package name in the manifest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipee.LoginActivity
import com.example.recipee.MainActivity
import com.example.recipee.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SigninActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_activity)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val username = findViewById<EditText>(R.id.newUsername)
        val password = findViewById<EditText>(R.id.newPassword)
        val passwordCheck = findViewById<EditText>(R.id.password_check)
        val email = findViewById<EditText>(R.id.newEmail)
        val signinButton = findViewById<Button>(R.id.signin_button)

        signinButton.setOnClickListener {
            val usernameText = username.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val passwordCheckText = passwordCheck.text.toString().trim()

            // Validate input fields
            if (usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || passwordCheckText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText != passwordCheckText) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Register user with Firebase Authentication
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Save username to Firebase Realtime Database
                        val userId = auth.currentUser?.uid
                        val database = FirebaseDatabase.getInstance().reference
                        val user = User(usernameText, emailText)

                        userId?.let {
                            database.child("users").child(it).setValue(user)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                        // Navigate to main activity
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // User data class to store user information
    data class User(
        val username: String? = "",
        val email: String? = ""
    )
}