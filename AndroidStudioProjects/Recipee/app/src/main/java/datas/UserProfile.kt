package datas

import com.google.firebase.Timestamp


// Assuming UserProfile is already defined
data class UserProfile(
    var userId: String = "",
    var username: String = "",
    var useremail: String = "",
    var profilePictureUrl: String = "",
    var points: Int = 50,
    var badge: String = calculateBadge(points), // Badge derived dynamically
    var posts: List<String> = emptyList(),
    var likedRecipes: List<String> = emptyList(),
    var bookmarked: List<String> = emptyList(),
    var lastLogin: Timestamp = Timestamp.now()
) {
    companion object {
        fun calculateBadge(points: Int): String {
            return when {
                points < 150 -> "Bronze"
                points < 300 -> "Silver"
                points < 600 -> "Gold"
                points < 1000 -> "Platinum"
                points < 2000 -> "Diamond"
                points < 5000 -> "Master"
                else -> "KingGodGeneral"
            }
        }
    }
}
