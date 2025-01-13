package Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipee.databinding.ItemIngredientBinding
import datas.RecipeIngredient

// IngredientsAdapter.kt
class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {
    private var ingredients = listOf<RecipeIngredient>()

    class IngredientViewHolder(private val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: RecipeIngredient) {
            binding.ingredientName.text = ingredient.name
            binding.ingredientAmount.text = ingredient.amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount() = ingredients.size

    fun submitList(newIngredients: List<RecipeIngredient>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }
}