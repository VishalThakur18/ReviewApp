package model
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class HomeCards(
    val dishName: String = "",
    val imageUrl: String = "",
    val restaurantName: String = ""
)