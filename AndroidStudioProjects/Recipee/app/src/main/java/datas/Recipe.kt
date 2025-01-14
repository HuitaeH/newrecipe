package datas

import com.google.firebase.Timestamp
import datas.RecipeIngredient  // Import the RecipeIngredient class
import datas.RecipeCategory    // Import the RecipeCategory enum

data class Recipe(
    var id: Long = 0,
    var title: String = "",
    var imageUrl: String = "",
    var totalTime: Int = 0,
    var cookingTime: Int = 0,
    var description: String = "",
    var ingredients: List<RecipeIngredient> = emptyList(),  // Using RecipeIngredient here
    var category: List<RecipeCategory> = emptyList(),  // Using RecipeCategory here
    var authorName: String = "",
    var authorImageUrl: String = "",
    var badge: String = "",
    var isBookmarked: Boolean = false,
    var uploadTime: Timestamp = Timestamp.now()
)


