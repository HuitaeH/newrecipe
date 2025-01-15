package com.example.recipee

import Adapter.HotRecipeAdapter
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
import android.widget.TextView
import com.example.recipee.databinding.FragmentHotBinding


class HotFragment : Fragment() {
    private var _binding: FragmentHotBinding? = null
    private val binding get() = _binding!!
    private val recipeAdapter = HotRecipeAdapter() // 기존 RecipeAdapter를 HotRecipeAdapter로 변경
    private var allRecipes: List<Recipe> = emptyList() // Initialize empty list

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView for recipes
        binding.recipesRecyclerView.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Set the text in the TextView
//        binding.replacementText.text = "Welcome to Hot Recipes!"

        // Load sample data with ranking
        loadSampleData()
    }

    // Load and rank sample data
    private fun loadSampleData() {
        val sampleRecipes = listOf(
            Recipe(
                id = 1,
                title = "Spiced Fried Chicken",
                cookingTime = 30,
                authorName = "Huitae",
                likeCount = 15,
                category = "diet",
                imageResId = R.drawable.chicken,
                profileImageResId = R.drawable.profile1,
                ingredients = listOf(
                    RecipeIngredient(name = "Chicken", amount = "1 kg"),
                    RecipeIngredient(name = "Chili Powder", amount = "2 tbsp"),
                    RecipeIngredient(name = "Garlic", amount = "3 cloves"),
                    RecipeIngredient(name = "Salt", amount = "1 tsp")
                ),

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
                ingredients = listOf(
                    RecipeIngredient(name = "Pasta", amount = "200 g"),
                    RecipeIngredient(name = "Garlic", amount = "2 cloves"),
                    RecipeIngredient(name = "Vegan Cream", amount = "100 ml"),
                    RecipeIngredient(name = "Olive Oil", amount = "1 tbsp")
                ),

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
                ingredients = listOf(
                    RecipeIngredient(name = "Bread", amount = "2 slices"),
                    RecipeIngredient(name = "Lettuce", amount = "2 leaves"),
                    RecipeIngredient(name = "Tomato", amount = "1 slice"),
                    RecipeIngredient(name = "Avocado", amount = "1/2")
                ),
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
                ingredients = listOf(
                    RecipeIngredient(name = "Rice", amount = "1 cup"),
                    RecipeIngredient(name = "Carrot", amount = "1 medium"),
                    RecipeIngredient(name = "Peas", amount = "1/2 cup"),
                    RecipeIngredient(name = "Soy Sauce", amount = "1 tbsp")
                ),
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
                ingredients = listOf(
                    RecipeIngredient(name = "Rice", amount = "1 cup"),
                    RecipeIngredient(name = "Nori", amount = "1 sheet"),
                    RecipeIngredient(name = "Chili Sauce", amount = "1 tbsp"),
                    RecipeIngredient(name = "Vinegar", amount = "1 tsp")
                ),
            )
        )

        // Rank the recipes by likeCount
        val rankedRecipes = sampleRecipes.sortedByDescending { it.likeCount }

        // Submit the ranked list to the adapter
        recipeAdapter.submitList(rankedRecipes)
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

//// Recipe data class with added 'rank' field
//data class Recipe(
//    val userId: String = "",
//    val documentId: String = "",
//    val id: Long = 0,
//    val title: String,
//    val cookingTime: Int,
//    val description: String = "",
//    val ingredients: List<RecipeIngredient> = emptyList(),
//    val category: String,
//    val authorName: String,
//    val authorImageUrl: String? = null,
//    val uploadTime: Timestamp = Timestamp.now(),
//    val isLiked: Boolean = false,
//    val likeCount: Int,
//    val rank: Int? = null // Added rank
//)
