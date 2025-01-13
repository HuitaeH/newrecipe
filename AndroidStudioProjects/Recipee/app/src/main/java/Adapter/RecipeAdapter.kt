package Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipee.R
import datas.Recipe
import com.example.recipee.databinding.ItemRecipeBinding

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private val recipes = mutableListOf<Recipe>()

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            with(binding) {
                recipeTitle.apply {
                    text = recipe.title
                    setTextColor(Color.parseColor("#1F1717"))  // 여기에 색상 설정 추가
                }
                recipeBadge.text = recipe.badge
                recipeTime.text = "${recipe.cookingTime} min"
                authorName.apply {
                    text = recipe.authorName
                    setTextColor(Color.parseColor("#1F1717"))  // 여기에 색상 설정 추가

                }

                // 이미지 로딩은 Glide 또는 Coil 라이브러리 사용 필요
                // Glide.with(itemView).load(recipe.imageUrl).into(recipeImage)
                // Glide.with(itemView).load(recipe.authorImageUrl).into(profileImage)

                bookmarkButton.setImageResource(
                    if (recipe.isBookmarked) R.drawable.ic_bookmark
                    else R.drawable.ic_bookmark
                )

                bookmarkButton.setOnClickListener {
                    recipe.isBookmarked = !recipe.isBookmarked
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size

    fun submitList(newRecipes: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }
}