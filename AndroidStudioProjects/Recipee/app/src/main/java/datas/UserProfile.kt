import com.google.firebase.Timestamp

data class UserProfile(
    var userId: String = "",
    var username: String = "",
    var profilePictureUrl: String = "",
    var points: Int = 50,
    var badge: List<String> = listOf("Beginner"),
    var posts: List<String> = emptyList(),
    var likes: Int = 0,
    var lastLogin: Timestamp = Timestamp.now()  // Firebase Timestamp as default
)
