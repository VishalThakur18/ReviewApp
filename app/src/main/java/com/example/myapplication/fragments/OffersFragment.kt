package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anupkumarpanwar.scratchview.ScratchView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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

        adapter = OfferPageAdapter(offerList)
        newRecyclerView.adapter = adapter

        // Load offers from Firestore
        fetchOffersFromFirestore()

        setupScratchCard(view)
        loadBannerImage(view)

        return view
    }

    private fun fetchOffersFromFirestore() {
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
                adapter.notifyDataSetChanged() // Update RecyclerView
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching offers: ${exception.message}")
                Toast.makeText(requireContext(), "Failed to load offers.", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "You have already scratched today. Come back tomorrow!", Toast.LENGTH_LONG).show()
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
