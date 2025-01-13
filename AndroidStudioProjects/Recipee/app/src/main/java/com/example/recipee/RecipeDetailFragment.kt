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
            title = "스파이시 프라이드 치킨",
            imageUrl = "sample_url",
            totalTime = 45,
            cookingTime = 30,
            description = "바삭바삭하고 매콤한 치킨 레시피입니다.",
            ingredients = listOf(
                RecipeIngredient("닭", "1마리"),
                RecipeIngredient("튀김가루", "2컵"),
                RecipeIngredient("우유", "1컵"),
                RecipeIngredient("고추가루", "2큰술")
            ),
            category = "다이어트"
        )

        updateUI(recipe)
    }

    private fun updateUI(recipe: RecipeDetail) {
        with(binding) {
            recipeTitle.text = recipe.title
            totalTime.text = "${recipe.totalTime}min"
            description.text = recipe.description
            ingredientsAdapter.submitList(recipe.ingredients)

            categoryTag.text = recipe.category  // 선택된 하나의 카테고리만 텍스트로 표시

            // Glide로 이미지 로드
            Glide.with(this@RecipeDetailFragment)
                .load(recipe.imageUrl)
                .into(recipeImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}