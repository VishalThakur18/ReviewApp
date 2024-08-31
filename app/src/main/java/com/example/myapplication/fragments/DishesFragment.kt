package com.example.myapplication.fragments

import ReviewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentDishesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import model.DishReview

class DishesFragment : Fragment() {

    private var _binding: FragmentDishesBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewsRecyclerView: RecyclerView
    private var reviewList = mutableListOf<DishReview>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDishesBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()

        reviewsRecyclerView = binding.dishesrecyclerView
        reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reviewAdapter = ReviewAdapter(reviewList, requireContext())
        reviewsRecyclerView.adapter = reviewAdapter

        fetchReviews()

        return binding.root
    }

    private fun fetchReviews() {
        firestore.collection("dishReview")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                reviewList.clear() // Clear the list before adding new data
                for (document in documents) {
                    val review = document.toObject(DishReview::class.java).apply {
                        id = document.id // Ensure the ID is set from Firestore
                    }
                    reviewList.add(review)
                }
                reviewAdapter.updateReviews(reviewList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
