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
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID


import datas.Recipe
import datas.RecipeCategory
import datas.RecipeIngredient

class PostRecipeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // For image selection
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_recipe_activity)

        val titleInput: EditText = findViewById(R.id.titleInput)
        val cookingTimeInput: EditText = findViewById(R.id.cookingTimeInput)
        val badgeInput: EditText = findViewById(R.id.badgeInput)
        val ingredientInput: EditText = findViewById(R.id.ingredientInput)
        val categoryInput: EditText = findViewById(R.id.categoryInput)
        val totalTimeInput: EditText = findViewById(R.id.totalTimeInput)
        val descriptionInput: EditText = findViewById(R.id.descriptionInput)
        val submitButton: Button = findViewById(R.id.submitButton)
        val gobackButton: Button = findViewById(R.id.gobackButton)

        submitButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val cookingTime = cookingTimeInput.text.toString().toIntOrNull() ?: 0
            val badge = badgeInput.text.toString().trim()

            // Get raw inputs for ingredients and categories
            val ingredientsInput = ingredientInput.text.toString()
            val categoriesInput = categoryInput.text.toString()

            // Ensure ingredientsInput is not empty before splitting
            val ingredients = if (ingredientsInput.isNotEmpty()) {
                ingredientsInput.split(",").map {
                    val parts = it.split(":")
                    if (parts.size == 2) {
                        RecipeIngredient(parts[0].trim(), parts[1].trim())
                    } else {
                        // Handle invalid input format here, e.g., show a message or skip invalid entries
                        RecipeIngredient("Unknown", "Unknown")
                    }
                }
            } else {
                emptyList<RecipeIngredient>()
            }

            // Ensure categoriesInput is not empty before splitting
            val categories = if (categoriesInput.isNotEmpty()) {
                categoriesInput.split(",").map { it.trim() }
                    .mapNotNull { categoryString ->
                        // Try to map the string into an enum value
                        try {
                            RecipeCategory.valueOf(categoryString.uppercase())
                        } catch (e: IllegalArgumentException) {
                            // Handle invalid category string (e.g., show error or skip invalid input)
                            null
                        }
                    }
            } else {
                emptyList<RecipeCategory>()
            }

            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid // Get the unique user ID
                val authorName = user.displayName ?: "Anonymous" // Optional: Get user's display name

                // Create a Recipe object
                val recipe = Recipe(
                    id = System.currentTimeMillis(),
                    title = title,
                    imageUrl = "", // Will be set later if the user selects an image
                    cookingTime = cookingTime,
                    ingredients = ingredients,  // List<RecipeIngredient>
                    category = categories,      // List<String>
                    authorName = authorName,
                    authorImageUrl = "",       // Optional: Add functionality for author image
                    badge = badge,
                    isBookmarked = false,
                    uploadTime = Timestamp.now()
                )

                // Save recipe to Firestore
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

    // Function to upload a profile picture (image) if needed
    private fun uploadRecipeImage(imageUri: Uri, callback: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("recipe_images/${UUID.randomUUID()}")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error uploading recipe image", e)
                callback(null)
            }
    }

    // For handling image selection
    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            // Optionally, upload image to Firebase Storage and update the recipe object
        }
    }
}