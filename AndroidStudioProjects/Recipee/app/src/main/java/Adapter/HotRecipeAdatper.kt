package Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipee.databinding.ItemHotBinding
import com.example.recipee.databinding.ItemRecipeBinding
import datas.Recipe

class HotRecipeAdapter : RecyclerView.Adapter<HotRecipeAdapter.HotRecipeViewHolder>() {

    private val recipeList = mutableListOf<Recipe>()

    class HotRecipeViewHolder(private val binding: ItemHotBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.title
            binding.likeCount.text = "❤️ ${recipe.likeCount} likes"
            binding.category.text = "${recipe.category}"
            binding.recipeImage.setImageResource(recipe.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotRecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHotBinding.inflate(inflater, parent, false)
        return HotRecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HotRecipeViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }

    override fun getItemCount(): Int = recipeList.size

    fun submitList(recipes: List<Recipe>) {
        recipeList.clear()
        recipeList.addAll(recipes)
        notifyDataSetChanged()
    }
}
