package datas

data class Recipe(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val cookingTime: Int,
    val authorName: String,
    val authorImageUrl: String,
    val badge: String,
    var isBookmarked: Boolean = false
)
