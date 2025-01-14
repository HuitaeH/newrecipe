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
import com.google.firebase.firestore.FirebaseFirestore
import datas.UserProfile
import com.google.firebase.Timestamp


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
                        val database = FirebaseFirestore.getInstance()
//                        val user = User(usernameText, emailText)

                        // Create a default user profile with additional fields
                        val userProfile = UserProfile(
                            userId = userId ?: "",
                            username = usernameText,
                            useremail = emailText,
                            profilePictureUrl = "", // You can set a default image URL here if needed
                            points = 50,  // Default points
                            badge = UserProfile.calculateBadge(50),  // Calculate badge based on points
                            posts = emptyList(),  // Empty list of posts initially
                            likedRecipes = emptyList(),  // Empty list of liked recipes
                            bookmarked = emptyList(),  // Empty list of bookmarked recipes
                            lastLogin = Timestamp.now()  // Current timestamp
                        )

                        userId?.let {
                            // Store user data (username and email) in Firestore under 'users' collection
                            database.collection("profile").document(it).set(userProfile)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                        // Navigate to login activity
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

}