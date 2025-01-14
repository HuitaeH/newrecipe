package datas

data class Recipe(
    var id: Long = 0,
    var title: String = "",
    var imageUrl: String = "",
    var cookingTime: Int = 0,
    var authorName: String = "",
    var authorImageUrl: String = "",
    var badge: String = "",
    var isLiked: Boolean = false,
    var likeCount: Int = 0,
    var isBookmarked: Boolean = false
)
