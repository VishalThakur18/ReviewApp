// DishReview.kt
package model

data class DishReview(
    val dishName: String,
    val price: Float,
    val restaurantName: String,
    val reviewText: String,
    val rating: Float,
    val imageUrl: String
)
