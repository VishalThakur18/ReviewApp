package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.DishReview

class ReviewAdapter(
    private var reviewList: List<DishReview>,
    private val context: Context
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private var currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishName: TextView = itemView.findViewById(R.id.Dish_name)
        val restaurantName: TextView = itemView.findViewById(R.id.Restraunt_Name)
        val userName: TextView = itemView.findViewById(R.id.User_name)
        val reviewText: TextView = itemView.findViewById(R.id.Review_text)
        val foodImage: ImageView = itemView.findViewById(R.id.Food_image)
        val priceOnCard: TextView = itemView.findViewById(R.id.priceOnCard)
        val userProfilePic: ImageView = itemView.findViewById(R.id.User_profile_pic)
        val likeBtn: ImageView = itemView.findViewById(R.id.like_btn)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteBtn)
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_feed, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]

        holder.dishName.text = review.dishName
        holder.restaurantName.text = review.restaurantName
        holder.userName.text = review.userName
        holder.reviewText.text = review.reviewText
        holder.priceOnCard.text = "Rs.${review.price}"
        holder.likeCount.text = review.likes.toString()
        holder.ratingTextView.text=review.rating.toString()

        val backgroundColor = when (review.rating) {
            0 -> R.drawable.redrectangle
            1 -> R.drawable.redrectangle
            2 -> R.drawable.orangerectangle
            3 -> R.drawable.yellowrectangle
            else -> R.drawable.greenrectangle
        }
        holder.itemView.findViewById<ConstraintLayout>(R.id.background_color).setBackgroundResource(backgroundColor)
        if (review.imageUrl.isNotEmpty()) {
            holder.foodImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(review.imageUrl)
                .into(holder.foodImage)
        } else {
            holder.foodImage.visibility = View.GONE
        }

        Glide.with(holder.itemView.context)
            .load(review.userProfilePic)
            .into(holder.userProfilePic)

        // Set like button state based on isLikedByUser
        updateLikeButtonState(holder, review.isLikedByUser(currentUserId))


        // GestureDetector to detect double tap
        val gestureDetector = GestureDetector(holder.itemView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                toggleLike(holder, review)
                return true
            }
        })

        holder.itemView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }

        holder.likeBtn.setOnClickListener {
            toggleLike(holder, review)
        }

        holder.deleteBtn.setOnClickListener {
            if (userId == review.userId) {
                showDeleteConfirmationDialog(review.id, holder.adapterPosition)
            } else {
                Toast.makeText(context, "You can only delete your own reviews.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLikeButtonState(holder: ReviewViewHolder, isLiked: Boolean) {
        holder.likeBtn.setImageResource(if (isLiked) R.drawable.liked_ic else R.drawable.like_ic)
    }

    private fun toggleLike(holder: ReviewViewHolder, review: DishReview) {
        currentUserId?.let { userId ->
            val wasLiked = review.isLikedByUser(userId)
            val newLikedBy = if (wasLiked) {
                review.likedBy - userId
            } else {
                review.likedBy + userId
            }

            val newLikes = if (wasLiked) review.likes - 1 else review.likes + 1

            // Update Firestore
            handleLikeClick(review.id, newLikedBy, newLikes)

            // Update local review object
            review.likedBy = newLikedBy
            review.likes = newLikes

            // Update UI
            updateLikeButtonState(holder, !wasLiked)
            showLikeCountTemporarily(holder, newLikes)
        }
    }

    private fun showLikeCountTemporarily(holder: ReviewViewHolder, likeCount: Int) {
        holder.likeCount.visibility = View.VISIBLE
        holder.likeCount.text = likeCount.toString()

    }

    override fun getItemCount() = reviewList.size

    fun updateReviews(newReviews: List<DishReview>) {
        reviewList = newReviews
        notifyDataSetChanged()
    }
    fun setCurrentUserId(userId: String?) {
        currentUserId = userId
        notifyDataSetChanged()  // Refresh the entire list to update like states
    }

    private fun handleLikeClick(reviewId: String, likedBy: List<String>, likes: Int) {
        val reviewRef = firestore.collection("dishReview").document(reviewId)
        reviewRef.update("likedBy", likedBy, "likes", likes)
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update like count: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleDeleteReview(reviewId: String) {
        val reviewRef = firestore.collection("dishReview").document(reviewId)
        reviewRef.delete()
            .addOnSuccessListener {
                reviewList = reviewList.filter { review -> review.id != reviewId }
                notifyDataSetChanged()
                Toast.makeText(context, "Review deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to delete review: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog(reviewId: String, position: Int) {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_review, null)

        // Create and configure the AlertDialog
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Find the dialog components
        val deleteButton = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.confirmDeleteButton)
        val cancelButton = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.cancelDeleteButton)

        // Set the Delete button click listener
        deleteButton.setOnClickListener {
            handleDeleteReview(reviewId)
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set the Cancel button click listener
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        // Show the dialog
        alertDialog.show()
    }

}