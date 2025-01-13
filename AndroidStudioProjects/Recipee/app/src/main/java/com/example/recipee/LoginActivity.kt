package com.example.recipee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val signinButton = findViewById<Button>(R.id.signin_button)

        signinButton.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            // Perform login with Firebase Authentication
            auth.signInWithEmailAndPassword(usernameText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User is authenticated, now save their info to the database
                        val userId = auth.currentUser?.uid
                        val userInfo = User(usernameText, usernameText) // You can save more info here
                        val database = FirebaseDatabase.getInstance()
                        val userRef = database.getReference("users")
                        userRef.child(userId!!).setValue(userInfo)

                        // Start MainActivity after successful login
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close the login activity so the user can't go back to it
                    } else {
                        // Handle failed login
                        Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show()
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
