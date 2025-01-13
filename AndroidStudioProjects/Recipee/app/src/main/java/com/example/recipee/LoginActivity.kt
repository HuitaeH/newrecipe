package com.example.recipee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            // Replace with actual login validation
            if (usernameText == "user" && passwordText == "password") {
                // Start main activity after successful login
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the login activity so the user can't go back to it
            } else {
                // Handle invalid login (e.g., show a toast or error message)
                Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
