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
import datas.Recipe


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val recipeAdapter = RecipeAdapter()
    private var allRecipes: List<Recipe> = emptyList()

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

        // 샘플 데이터 로드
        loadSampleData()
        // 검색 기능 추가
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterRecipes(query)
        }
    }

    private fun loadSampleData() {
            allRecipes = listOf(
            Recipe(
                id = 1,
                title = "Spiced Fried Chicken",
                imageUrl = "sample_url",
                cookingTime = 30,
                authorName = "Huitae",
                authorImageUrl = "profile_url",
                badge = "beginner"
            ),
            Recipe(
                id = 2,
                title = "Creamy Garlic Pasta",
                imageUrl = "sample_url",
                cookingTime = 20,
                authorName = "Minji",
                authorImageUrl = "profile_url",
                badge = "intermediate"
            )
            // 더 많은 샘플 데이터 추가
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