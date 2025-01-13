package com.example.recipee

import Adapter.RecipeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipee.databinding.FragmentHomeBinding
import android.widget.Button
import android.content.Intent

import datas.Recipe


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val recipeAdapter = RecipeAdapter()

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


    }

    private fun loadSampleData() {
        val sampleRecipes = listOf(
            Recipe(
                id = 1,
                title = "Spiced Fried Chicken",
                imageUrl = "sample_url",
                cookingTime = 30,
                authorName = "Yumna Azzahra",
                authorImageUrl = "profile_url",
                badge = "beginner"
            )
            // 더 많은 샘플 데이터 추가
        )
        recipeAdapter.submitList(sampleRecipes)
    }
}