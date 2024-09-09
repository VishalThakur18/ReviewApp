package model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class DishReview(
    var id: String = "",
    var dishName: String = "",
    var price: Int = 0,
    var rating: Double = 0.0,
    var likedBy: List<String> = emptyList(),
    var likes: Int = 0,
    val imageUrl: String = "",
    var restaurantName: String = "",
    var reviewText: String = "",
    var shopLocation: GeoPoint = GeoPoint(0.0, 0.0),
    @ServerTimestamp var timestamp: Date? = null,
    var userName: String = "",
    var userProfilePic: String = "",
    var userId: String = ""
) {
    fun isLikedByUser(currentUserId: String?): Boolean {
        return currentUserId != null && currentUserId in likedBy
    }
}