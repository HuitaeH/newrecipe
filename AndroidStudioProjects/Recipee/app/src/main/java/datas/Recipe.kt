package datas

import com.google.firebase.Timestamp
import datas.RecipeIngredient  // Import the RecipeIngredient class
import datas.RecipeCategory    // Import the RecipeCategory enum

data class Recipe(
    var userId: String = "",  // Add userId to store the UID of the user creating the recipe
    var documentId: String = "",
    var id: Long = 0,
    var title: String = "",
    var cookingTime: Int = 0,
    var description: String = "",
    var ingredients: List<RecipeIngredient> = emptyList(),  // Using RecipeIngredient here
    val category: String,  // Using RecipeCategory here
    var authorName: String = "",
    var isBookmarked: Boolean = false, //delete this!!!
    var uploadTime: Timestamp = Timestamp.now(),
    var isLiked: Boolean = false,
    var likeCount: Int = 0,
    val imageResId: Int,
    val profileImageResId: Int,
) {
    val categoryEnum: RecipeCategory
        get() = try {
            // Attempt to map the string to a RecipeCategory enum value
            RecipeCategory.valueOf(category.uppercase())  // Convert category to uppercase to avoid case mismatch
        } catch (e: IllegalArgumentException) {
            // Fallback if the category value is invalid or not found in the enum
            RecipeCategory.DIET  // Default category
        }
}
