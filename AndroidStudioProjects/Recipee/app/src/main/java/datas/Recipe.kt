package datas

import com.google.firebase.Timestamp
import datas.RecipeIngredient  // Import the RecipeIngredient class
import datas.RecipeCategory    // Import the RecipeCategory enum

data class Recipe(
    var id: Long = 0,
    var title: String = "",
    var imageUrl: String = "",
    var cookingTime: Int = 0,
    var description: String = "",
    var ingredients: List<RecipeIngredient> = emptyList(),  // Using RecipeIngredient here
    val category: String,  // Using RecipeCategory here
    var authorName: String = "",
    var authorImageUrl: String = "",
    var badge: String = "", //delete this!!!
    var isBookmarked: Boolean = false, //delete this!!!
    var uploadTime: Timestamp = Timestamp.now(),
    var userId: String = "",  // Add userId to store the UID of the user creating the recipe
    var isLiked: Boolean = false,
    var likeCount: Int = 0
)
