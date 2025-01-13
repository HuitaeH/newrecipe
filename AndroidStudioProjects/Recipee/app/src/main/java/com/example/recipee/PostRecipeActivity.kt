package com.example.recipee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import datas.Recipe

class PostRecipeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_recipe_activity)

        val titleInput: EditText = findViewById(R.id.titleInput)
        val cookingTimeInput: EditText = findViewById(R.id.cookingTimeInput)
        val badgeInput: EditText = findViewById(R.id.badgeInput)
        val submitButton: Button = findViewById(R.id.submitButton)
        val gobackButton: Button = findViewById(R.id.gobackButton)

        submitButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val cookingTime = cookingTimeInput.text.toString().toIntOrNull() ?: 0
            val badge = badgeInput.text.toString().trim()

            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid // Get the unique user ID
                val authorName = user.displayName ?: "Anonymous" // Optional: Get user's display name

                // Create a Recipe object
                val recipe = Recipe(
                    id = System.currentTimeMillis(),
                    title = title,
                    imageUrl = "", // Implement image selection if needed
                    cookingTime = cookingTime,
                    authorName = authorName,
                    authorImageUrl = "", // Add functionality for image upload
                    badge = badge,
                    isBookmarked = false
                )

                saveRecipeToFirestore(recipe, userId)
            } else {
                Toast.makeText(this, "User not signed in!", Toast.LENGTH_SHORT).show()
            }
        }

        gobackButton.setOnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveRecipeToFirestore(recipe: Recipe, userId: String) {
        db.collection("users").document(userId).collection("recipes").document(recipe.id.toString())
            .set(recipe)
            .addOnSuccessListener {
                Log.d("Firestore", "Recipe successfully added for user: $userId")
                Toast.makeText(this, "Recipe posted successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding recipe", e)
                Toast.makeText(this, "Failed to post recipe.", Toast.LENGTH_SHORT).show()
            }
    }
}
