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
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
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
    private var selectedCategory: String? = null  // 선택된 카테고리 저장

    // For image selection
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_recipe_activity)

        val titleInput: TextInputEditText = findViewById(R.id.titleInput)
        val ingredientInput: TextInputEditText = findViewById(R.id.ingredientInput)
        val cookingTimeInput: TextInputEditText = findViewById(R.id.cookingTimeInput)
        val descriptionInput: TextInputEditText = findViewById(R.id.descriptionInput)

        // 카테고리 칩 그룹 설정
        val chipDiet: Chip = findViewById(R.id.chip_diet)
        val chipVegan: Chip = findViewById(R.id.chip_vegan)
        val chipHealth: Chip = findViewById(R.id.chip_health)

        val submitButton: MaterialButton = findViewById(R.id.submitButton)
        val gobackButton: MaterialButton = findViewById(R.id.gobackButton)

        // 카테고리 칩 리스너 설정
        chipDiet.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                chipVegan.isChecked = false
                chipHealth.isChecked = false
                selectedCategory = "Diet"
            }
        }

        chipVegan.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                chipDiet.isChecked = false
                chipHealth.isChecked = false
                selectedCategory = "Vegan"
            }
        }

        chipHealth.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                chipDiet.isChecked = false
                chipVegan.isChecked = false
                selectedCategory = "Health"
            }
        }

        submitButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val cookingTime = cookingTimeInput.text.toString().toIntOrNull() ?: 0
            val description = descriptionInput.text.toString().trim()
            val ingredientsInput = ingredientInput.text.toString()

            if (selectedCategory == null) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 재료 처리
            val ingredients = if (ingredientsInput.isNotEmpty()) {
                ingredientsInput.split(",").map {
                    val parts = it.split(":")
                    if (parts.size == 2) {
                        RecipeIngredient(parts[0].trim(), parts[1].trim())
                    } else {
                        RecipeIngredient("Unknown", "Unknown")
                    }
                }
            } else {
                emptyList()
            }

            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid
                val authorName = user.displayName ?: "Anonymous"

                val recipe = Recipe(
                    id = System.currentTimeMillis(),
                    title = title,
                    imageUrl = "",
                    cookingTime = cookingTime,
                    ingredients = ingredients,
                    authorName = authorName,
                    authorImageUrl = "",
                    category = selectedCategory!!,  // 선택된 카테고리 추가
                    isBookmarked = false,
                    uploadTime = Timestamp.now()
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