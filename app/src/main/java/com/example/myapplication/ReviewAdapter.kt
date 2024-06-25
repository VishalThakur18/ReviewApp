package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.DishReview

class ReviewAdapter(private var reviewList: List<DishReview>, private val context: Context) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishName: TextView = itemView.findViewById(R.id.Dish_name)
        val userName: TextView = itemView.findViewById(R.id.User_name)
        val reviewText: TextView = itemView.findViewById(R.id.Review_text)
        val foodImage: ImageView = itemView.findViewById(R.id.Food_image)
        val priceOnCard: TextView = itemView.findViewById(R.id.priceOnCard)
        val rating: RatingBar = itemView.findViewById(R.id.rating)
        val userProfilePic: ImageView = itemView.findViewById(R.id.User_profile_pic)
        val likeBtn: ImageView = itemView.findViewById(R.id.like_btn)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_feed, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        holder.dishName.text = review.dishName
        holder.userName.text = review.userName
        holder.reviewText.text = review.reviewText
        holder.priceOnCard.text = "Rs.${review.dishPrice}"
        holder.rating.rating = review.dishRating.toFloat()
        holder.likeCount.text = review.likedBy.size.toString()

        Glide.with(holder.itemView.context)
            .load(review.imageUrl)
            .into(holder.foodImage)

        Glide.with(holder.itemView.context)
            .load(review.userProfilePic)
            .into(holder.userProfilePic)

        holder.likeBtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val mutableLikedBy = review.likedBy.toMutableList() // Convert to mutable list

            if (mutableLikedBy.contains(userId)) {
                // User has already liked the review, so unlike it
                mutableLikedBy.remove(userId)
            } else {
                // User has not liked the review, so like it
                mutableLikedBy.add(userId)
            }

            handleLikeClick(review.id, mutableLikedBy)
        }
    }

    override fun getItemCount() = reviewList.size

    fun updateReviews(newReviews: List<DishReview>) {
        reviewList = newReviews
        notifyDataSetChanged()
    }

    private fun handleLikeClick(reviewId: String, likedBy: List<String>) {
        val reviewRef = firestore.collection("dishReview").document(reviewId)
        reviewRef.update("likedBy", likedBy)
            .addOnSuccessListener {
                notifyDataSetChanged() // Update the UI
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update like count: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
