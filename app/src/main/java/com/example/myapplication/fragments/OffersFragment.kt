package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anupkumarpanwar.scratchview.ScratchView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.OfferDetailActivity
import com.example.myapplication.OfferPageAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.OffersFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import model.Offer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OffersFragment : Fragment() {
    private var _binding: OffersFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var adapter: OfferPageAdapter
    private val offerList = mutableListOf<Offer>()
    private val scratchPrefKey = "SCRATCH_PREF_KEY"
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OffersFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up RecyclerView
        newRecyclerView = view.findViewById(R.id.recyclerReview)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.setHasFixedSize(true)

        adapter = OfferPageAdapter(offerList) { clickedOffer ->
            // Create an intent to open the OfferDetailActivity
            val intent = Intent(requireContext(), OfferDetailActivity::class.java)
            intent.putExtra("title", clickedOffer.restaurantName)
            intent.putExtra("desc", clickedOffer.offerDescription)
            intent.putExtra("image", clickedOffer.imageUrl) // Assuming it's a drawable resource ID or URL
//            intent.putExtra("location", clickedOffer.location) // Pass the location
            intent.putExtra("name", clickedOffer.expireDate)

            startActivity(intent)
        }
        newRecyclerView.adapter = adapter

        // Load offers from Firestore
        fetchOffersFromFirestore()

        setupScratchCard(view)
        loadBannerImage(view)

        return view
    }

    private fun fetchOffersFromFirestore() {
        // Start shimmer animation
        val shimmerContainer = binding.shimmerContainer
        val recyclerView = binding.recyclerReview

        val shimmerAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.shimmer_animation)
        shimmerContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // Apply the shimmer animation to the entire container
        shimmerContainer.startAnimation(shimmerAnimation)
        firestore.collection("offers")
            .get()
            .addOnSuccessListener { querySnapshot ->
                offerList.clear()
                for (document in querySnapshot.documents) {
                    val offer = document.toObject(Offer::class.java)
                    if (offer != null) {
                        offerList.add(offer)
                    }
                }

                // Sort the offers based on expireDate
                offerList.sortWith { offer1, offer2 ->
                    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault()) // Update the format

                    try {
                        val date1 = dateFormat.parse(offer1.expireDate)
                        val date2 = dateFormat.parse(offer2.expireDate)
                        when {
                            date1 == null && date2 == null -> 0
                            date1 == null -> 1
                            date2 == null -> -1
                            else -> date1.compareTo(date2)
                        }
                    } catch (e: Exception) {
                        // Handle any parsing errors (invalid date format, etc.)
                        Log.e("Date Parsing", "Error parsing date: ${e.message}")
                        0
                    }
                }

                // Notify adapter of data change
                adapter.notifyDataSetChanged() // Update RecyclerView

                // Stop shimmer animation and show RecyclerView
                shimmerContainer.clearAnimation()
                shimmerContainer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching offers: ${exception.message}")
                Toast.makeText(requireContext(), "Failed to load offers.", Toast.LENGTH_SHORT).show()

                // Stop shimmer animation even if data fails to load
                shimmerContainer.clearAnimation()
                shimmerContainer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
    }



    private fun setupScratchCard(view: View) {
        val scratchView: ScratchView = view.findViewById(R.id.scratchView)

        // Get current date and last scratch date
        val currentDate = getCurrentDate()
        val sharedPreferences = requireContext().getSharedPreferences("ScratchPrefs", Context.MODE_PRIVATE)
        val lastScratchDate = sharedPreferences.getString(scratchPrefKey, "")

        if (lastScratchDate == currentDate) {
            scratchView.visibility = View.GONE
        } else {
            scratchView.setRevealListener(object : ScratchView.IRevealListener {
                override fun onRevealed(scratchView: ScratchView) {
                    Toast.makeText(requireContext(), "Revealed", Toast.LENGTH_LONG).show()
                    with(sharedPreferences.edit()) {
                        putString(scratchPrefKey, currentDate)
                        apply()
                    }
                }

                override fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float) {
                    if (percent >= 0.5) {
                        Log.d("Reveal Percentage", "onRevealPercentChangedListener: $percent")
                        scratchView.reveal()
                    }
                }
            })
        }
    }

    private fun loadBannerImage(view: View) {
        if (auth.currentUser != null) {
            val bannerImageView: ImageView = view.findViewById(R.id.imageView5)
            val bannerImageRef = storageReference.child("deal_of_the_day/deal.jpg")
            val startTime = System.currentTimeMillis()

            bannerImageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Firebase", "Image URL fetched in ${System.currentTimeMillis() - startTime} ms")
                Glide.with(this)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(bannerImageView)
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to get image URL: ${exception.message}")
                Toast.makeText(requireContext(), "Failed to load banner image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User is not authenticated. Please log in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
