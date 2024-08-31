package model

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
    var isLikedByUser: Boolean = false, // Added field to store like state
    val imageUrl: String = "",
    var restaurantName: String = "",
    var reviewText: String = "",
    var shopLocation: GeoPoint = GeoPoint(0.0, 0.0),
    @ServerTimestamp var timestamp: Date? = null,
    var userName: String = "",
    var userProfilePic: String = "",
    var userId: String = "" // Add userId field to store the ID of the user who created the review
)
