package datas

// RecipeCategory.kt
enum class RecipeCategory {
    DIET, VEGAN, HEALTH
}

// RecipeDetail.kt
data class RecipeDetail(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val totalTime: Int,
    val cookingTime: Int,
    val description: String,
    val ingredients: List<RecipeIngredient>,
    val category: String
)
