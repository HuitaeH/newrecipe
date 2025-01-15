package com.example.recipee

import Adapter.IngredientsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.recipee.databinding.FragmentRecipeDetailBinding
import datas.RecipeCategory
import datas.RecipeDetail
import datas.RecipeIngredient

// RecipeDetailFragment.kt
class RecipeDetailFragment : Fragment() {
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val ingredientsAdapter = IngredientsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadRecipeDetail() // 실제로는 ID를 받아서 해당 레시피 정보를 로드
    }

    private fun setupRecyclerView() {
        binding.ingredientsRecycler.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadRecipeDetail() {
        // 샘플 데이터
        val recipe = RecipeDetail(
            id = 1,
            title = "Spiced Fried Chicken",
            imageResId = R.drawable.chicken,
            totalTime = 20,
            description = "Spiced Fried Chicken is crispy, golden chicken seasoned with flavorful spices. It’s juicy inside, crunchy outside, and delicious to eat!",
            ingredients = listOf(
                RecipeIngredient("Chicken", "1"),
                RecipeIngredient("Batter mix", "2 cups"),
                RecipeIngredient("Milk", "1 cup"),
                RecipeIngredient("Chilli powder", "2 tsp")
            ),
            category = "Diet"
        )
        updateUI(recipe)
    }

    private fun updateUI(recipe: RecipeDetail) {
        with(binding) {
            recipeTitle.text = recipe.title
            totalTime.text = "${recipe.totalTime}min"
            description.text = recipe.description
            ingredientsAdapter.submitList(recipe.ingredients)
            recipeImage.setImageResource(recipe.imageResId)
            categoryTag.text = recipe.category


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}