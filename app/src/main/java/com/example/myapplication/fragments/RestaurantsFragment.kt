package com.example.myapplication.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.RestaurantAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import model.Restaurant
import java.util.Calendar
import java.util.concurrent.TimeUnit

class RestaurantsFragment : Fragment() {

    private lateinit var daysTextView: TextView
    private lateinit var hoursTextView: TextView
    private lateinit var minutesTextView: TextView
    private lateinit var secondsTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var restaurantOfTheWeekName: TextView
    private lateinit var restaurantOfTheWeekImage: ImageView
    private val restaurantList = mutableListOf<Restaurant>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restraunts, container, false)

        // Initialize views
        daysTextView = view.findViewById(R.id.daysTextView)
        hoursTextView = view.findViewById(R.id.hoursTextView)
        minutesTextView = view.findViewById(R.id.minutesTextView)
        secondsTextView = view.findViewById(R.id.secondsTextView)
        restaurantOfTheWeekName = view.findViewById(R.id.restaurantOfTheWeekName)
        restaurantOfTheWeekImage = view.findViewById(R.id.restaurantOfTheWeekImage)

        // Get current user ID
        currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(context, "Please log in to vote!", Toast.LENGTH_SHORT).show()
            return view
        }

        // Countdown Timer
        val timeUntilNextSunday = calculateTimeUntilNextSundayMidnight()
        startCountdownTimer(timeUntilNextSunday)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.restrauntRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RestaurantAdapter(restaurantList) { restaurant ->
            showVoteConfirmationDialog(restaurant)
        }
        recyclerView.adapter = adapter

        // Load restaurants
        loadRestaurants()

        // Fetch restaurant of the week
        fetchRestaurantOfTheWeek()

        return view
    }

    private fun calculateTimeUntilNextSundayMidnight(): Long {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysUntilSunday = if (currentDayOfWeek == Calendar.SUNDAY) 0 else 8 - currentDayOfWeek
        calendar.add(Calendar.DAY_OF_YEAR, daysUntilSunday)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis - System.currentTimeMillis()
    }

//    private fun loadRestaurants() {
//        db.collection("restaurants")
//            .orderBy("votes", Query.Direction.DESCENDING) // Order by votes in descending order
//            .get()
//            .addOnSuccessListener { snapshot ->
//                restaurantList.clear() // Clear the existing list
//                for (document in snapshot.documents) {
//                    val restaurant = document.toObject(Restaurant::class.java)?.apply {
//                        id = document.id // Set the auto-generated document ID
//                    }
//                    restaurant?.let { restaurantList.add(it) }
//                }
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Failed to load restaurants.", Toast.LENGTH_SHORT).show()
//            }
//    }
    private fun loadRestaurants() {
        db.collection("restaurants")
            .orderBy("votes", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Failed to load restaurants.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    restaurantList.clear()
                    for (document in snapshot.documents) {
                        val restaurant = document.toObject(Restaurant::class.java)?.apply {
                            id = document.id
                        }
                        restaurant?.let { restaurantList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }


    private fun fetchRestaurantOfTheWeek() {
        db.collection("restaurants")
            .orderBy("votes", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    val restaurant = snapshot.documents[0].toObject(Restaurant::class.java)
                    restaurantOfTheWeekName.text = restaurant?.name ?: "No Restaurant"
                    Glide.with(this)
                        .load(restaurant?.imageUrl)
                        .into(restaurantOfTheWeekImage)

                    // Fetch and display the number of votes
                    val votesCount = restaurant?.votes ?: 0
                    val votesOnRestOfWeek = view?.findViewById<TextView>(R.id.votesOnRestOfWeek)
                    votesOnRestOfWeek?.text = "$votesCount Votes"
                }
            }
            .addOnFailureListener { e->
                Log.e("FetchRestaurantOfTheWeek", "Error fetching restaurant of the week: $e")
            }
    }

    private fun showVoteConfirmationDialog(restaurant: Restaurant) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.restraunt_voting_dialog,null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
        // Get references to the views in the dialog layout
        val restaurantNameTextView = dialogView.findViewById<TextView>(R.id.nameRest)
        val restaurantImageView = dialogView.findViewById<ImageView>(R.id.restaurantImage)
        val confirmButton = dialogView.findViewById<TextView>(R.id.confirmBtn)
        val cancelButton = dialogView.findViewById<TextView>(R.id.cancelBtn)

        // Set the restaurant details dynamically
        restaurantNameTextView.text = "Vote for \"${restaurant.name}\", You Sure?"
        Glide.with(this)
            .load(restaurant.imageUrl) // Use Glide to load the image
            .into(restaurantImageView)

        // Create and show the dialog
        val dialog = dialogBuilder.create()

        // Handle button clicks
        confirmButton.setOnClickListener {
            handleVote(restaurant) // Submit the vote
            dialog.dismiss() // Close the dialog
        }

        cancelButton.setOnClickListener {
            dialog.dismiss() // Close the dialog without action
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun handleVote(restaurant: Restaurant) {
        if (restaurant.id.isEmpty() || currentUserId.isNullOrEmpty()) {
            Toast.makeText(context, "Invalid restaurant ID or user session.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentWeekStart = getStartOfCurrentWeekTimestamp()

        // Check if the user has voted for ANY restaurant this week
        db.collection("votes")
            .whereEqualTo("userId", currentUserId)
            .whereGreaterThanOrEqualTo("timestamp", currentWeekStart) // Check votes in the current week
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    Toast.makeText(context, "You have already voted for a restaurant this week!", Toast.LENGTH_SHORT).show()
                } else {
                    // User hasn't voted this week, allow voting
                    val voteData = hashMapOf(
                        "userId" to currentUserId,
                        "restaurantId" to restaurant.id,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("votes").add(voteData)
                        .addOnSuccessListener {
                            // Update the vote count for the restaurant
                            db.collection("restaurants").document(restaurant.id)
                                .update("votes", FieldValue.increment(1))
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Vote submitted successfully!", Toast.LENGTH_SHORT).show()

                                    // Add a listener to track the vote count in real-time
                                    db.collection("restaurants").document(restaurant.id)
                                        .addSnapshotListener { snapshot, error ->
                                            if (error != null) {
                                                Log.e("HandleVote", "Error fetching vote count: $error")
                                                return@addSnapshotListener
                                            }
                                            snapshot?.let {
                                                val updatedVotes = it.getLong("votes")?.toInt() ?: 0
                                                updateVoteCountUI(restaurant, updatedVotes) // Update the UI for this restaurant
                                            }
                                        }

                                    // Fetch the updated Restaurant of the Week
                                    fetchRestaurantOfTheWeek()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to update restaurant votes.", Toast.LENGTH_SHORT).show()
                                    Log.e("HandleVote", "Error updating votes: $e")
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to register your vote.", Toast.LENGTH_SHORT).show()
                            Log.e("HandleVote", "Error adding vote: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to check vote status.", Toast.LENGTH_SHORT).show()
                Log.e("HandleVote", "Error fetching votes: $e")
            }
    }


    private fun updateVoteCountUI(restaurant: Restaurant, newVoteCount: Int) {
        // Find the index of the restaurant in the list
        val index = restaurantList.indexOfFirst { it.id == restaurant.id }
        if (index != -1) {
            // Update the vote count in the adapter
            adapter.updateVoteCount(index, newVoteCount)
        }
    }



    private fun getStartOfCurrentWeekTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

private fun startCountdownTimer(timeInMillis: Long) {
    object : CountDownTimer(timeInMillis, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
            val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
            daysTextView.text = String.format("%02d", days)
            hoursTextView.text = String.format("%02d", hours)
            minutesTextView.text = String.format("%02d", minutes)
            secondsTextView.text = String.format("%02d", seconds)
        }

        override fun onFinish() {
            // Reset all restaurant votes to 0
            resetRestaurantVotes()

            // Restart the countdown timer for the next week
            val timeUntilNextSunday = calculateTimeUntilNextSundayMidnight()
            startCountdownTimer(timeUntilNextSunday)
        }
    }.start()
}

    private fun resetRestaurantVotes() {
        val batch = db.batch()

        // Fetch all restaurants and update their vote counts
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    val restaurantRef = db.collection("restaurants").document(document.id)
                    batch.update(restaurantRef, "votes", 0) // Reset the votes to 0
                }

                // Commit the batch operation to reset votes
                batch.commit()
                    .addOnSuccessListener {
                        Log.d("RestaurantsFragment", "Votes have been reset successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("RestaurantsFragment", "Error resetting votes: $e")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("RestaurantsFragment", "Error fetching restaurants: $e")
            }
    }

}
