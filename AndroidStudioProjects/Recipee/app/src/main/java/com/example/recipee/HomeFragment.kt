package com.example.recipee

import Adapter.RecipeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipee.databinding.FragmentHomeBinding
import android.widget.Button
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import android.widget.Toast
import com.google.firebase.Timestamp
import datas.RecipeCategory
import android.util.Log



import datas.Recipe
import datas.RecipeIngredient


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val recipeAdapter = RecipeAdapter()
    private var allRecipes: List<Recipe> = emptyList()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recipesRecyclerView.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Set up Add Recipe Button
        binding.addRecipeButton.setOnClickListener {
            // Navigate to PostRecipeActivity
            val intent = Intent(context, PostRecipeActivity::class.java)
            startActivity(intent)
        }

        // 샘플 데이터 로드
        loadSampleData()
        // 검색 기능 추가
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterRecipes(query)
        }
        // Fetch recipes from Firestore
        fetchRecipesFromFirestore()

        // Set up search functionality
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterRecipes(query)
        }
    }

    private fun getImageResId(imageName: String): Int {
        return when (imageName) {
            "spiced_fried_chicken" -> R.drawable.chicken
            "creamy_garlic_pasta" -> R.drawable.garlicpasta
            "delicious_sandwich" -> R.drawable.sandwich
            "fried_rice" -> R.drawable.friedrice
            "spicy_onigiri" -> R.drawable.onigiri
            else -> R.drawable.ic_home // 기본 이미지
        }
    }



    private fun fetchRecipesFromFirestore() {
        // Query the 'users' collection
        db.collection("users")  // Assuming your users are stored in 'users' collection
            .get()  // Get all documents in the 'users' collection
            .addOnSuccessListener { usersSnapshot ->
                if (usersSnapshot.isEmpty) {
                    Log.d("HomeFragment", "No users found.")
                    return@addOnSuccessListener
                }

                // Loop through each user document
                for (userDoc in usersSnapshot.documents) {
                    val userId = userDoc.id
                    Log.d("HomeFragment", "Fetching recipes for user: $userId")

                    // Query the 'recipes' subcollection for each user
                    db.collection("users").document(userId).collection("recipes")
                        .get()
                        .addOnSuccessListener { recipesSnapshot ->
                            if (recipesSnapshot.isEmpty) {
                                Log.d("HomeFragment", "No recipes found for user $userId")
                                return@addOnSuccessListener
                            }

                            val recipeList = recipesSnapshot.documents.mapNotNull { doc ->
                                // Get values from the recipe document
                                val title = doc.getString("title") ?: ""
                                val cookingTime = (doc.getLong("cookingTime") ?: 0).toInt()
                                val userId = doc.getString("userId") ?: ""
                                val description = doc.getString("description") ?: ""
                                val category = RecipeCategory.valueOf(doc.getString("category") ?: "DIET")

                                // Parse ingredients from Firestore if it's an array of maps
                                val ingredients: List<RecipeIngredient> = (doc.get("ingredients") as? List<Map<String, Any>> ?: emptyList())
                                    .map {
                                        RecipeIngredient(
                                            name = it["name"] as? String ?: "",
                                            amount = it["quantity"] as? String ?: ""
                                        )
                                    }

                                // Create Recipe object
                                Recipe(
                                    userId = userId,
                                    documentId = doc.id,
                                    id = doc.id.toLong(),  // Use Firestore document ID as the recipe ID
                                    title = title,
                                    cookingTime = cookingTime,
                                    description = description,
                                    ingredients = ingredients,
                                    category = category.name,  // Store category as a string in Firestore
                                    authorName = doc.getString("authorName") ?: "",
                                    profileImageResId = R.drawable.profile1,

                                    uploadTime = doc.getTimestamp("uploadTime") ?: Timestamp.now(),
                                    isLiked = doc.getBoolean("isLiked") ?: false,
                                    likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
                                    imageResId = getImageResId(doc.getString("imageName") ?: "default_image")
                                )
                            }

                            // Check if recipes are fetched and update UI
                            if (recipeList.isNotEmpty()) {
                                allRecipes = recipeList
                                recipeAdapter.submitList(allRecipes)  // Update RecyclerView with the fetched data
                            } else {
                                Log.d("HomeFragment", "No recipes available for user $userId")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("HomeFragment", "Error fetching recipes for user $userId: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error fetching users: ${e.message}")
            }
    }


    private fun loadSampleData() {
            allRecipes = listOf(
            Recipe(
                id = 1,
                title = "Spiced Fried Chicken",
                cookingTime = 30,
                authorName = "Huitae",
                likeCount = 15,
                category = "diet",
                imageResId = R.drawable.chicken,
                profileImageResId = R.drawable.profile1,
            ),
            Recipe(
                id = 2,
                title = "Creamy Garlic Pasta",
                cookingTime = 20,
                authorName = "Minji",
                likeCount = 8,
                category = "vegan",
                imageResId = R.drawable.garlicpasta,
                profileImageResId = R.drawable.profile2,
            ),
            Recipe(
                id = 3,
                title = "Delicious Sandwich",
                cookingTime = 10,
                authorName = "Minji",
                likeCount = 300,
                category = "health",
                imageResId = R.drawable.sandwich,
                profileImageResId = R.drawable.ic_logo,
            ),
            Recipe(
                id = 4,
                title = "Fried Rice with Vegetables",
                cookingTime = 30,
                authorName = "Jihoon",
                likeCount = 1,
                category = "vegan",
                imageResId = R.drawable.friedrice,
                profileImageResId = R.drawable.ic_logo,

            ),
            Recipe(
                id = 5,
                title = "Spicy and Sour Onigiri",
                cookingTime = 10,
                authorName = "Youngmin",
                likeCount = 25,
                category = "health",
                imageResId = R.drawable.onigiri,
                profileImageResId = R.drawable.ic_logo,

            )
        )
        recipeAdapter.submitList(allRecipes)
    }

    private fun filterRecipes(query: String) {
        val filteredRecipes = if (query.isEmpty()) {
            allRecipes
        } else {
            allRecipes.filter { it.title.contains(query, ignoreCase = true) }
        }
        recipeAdapter.submitList(filteredRecipes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}