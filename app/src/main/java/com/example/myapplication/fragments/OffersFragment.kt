package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anupkumarpanwar.scratchview.ScratchView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.R
import com.example.myapplication.ReviewCards
import com.example.myapplication.ReviewPageAdapter
import com.example.myapplication.databinding.OffersFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OffersFragment : Fragment() {
    private var _binding: OffersFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var cardList: ArrayList<ReviewCards>
    private lateinit var offerBackend: LinearLayout
    private val scratchPrefKey = "SCRATCH_PREF_KEY"
    private val storageReference = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val databaseRef = FirebaseDatabase.getInstance().getReference("offers")
    private val allowedEmail = "pramod.k.201555@gmail.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OffersFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_offersFragment_to_homeFragment)
        }

        offerBackend = view.findViewById(R.id.offerBackend)
        newRecyclerView = view.findViewById(R.id.recyclerReview)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.setHasFixedSize(true)

        cardList = arrayListOf()
//        populateCardList()
        // Adjust visibility based on the logged-in user's email
        val currentUser = auth.currentUser
        if (currentUser?.email == allowedEmail) {
            offerBackend.visibility = View.VISIBLE
            newRecyclerView.visibility = View.GONE
        } else {
            offerBackend.visibility = View.GONE
            newRecyclerView.visibility = View.VISIBLE
        }
        // Fetch offers from Firebase
        fetchOffers()

        newRecyclerView.adapter = ReviewPageAdapter(cardList)

        val scratchView: ScratchView = view.findViewById(R.id.scratchView)

        // Get the current date and the last scratch date from SharedPreferences
        val currentDate = getCurrentDate()
        val sharedPreferences = requireContext().getSharedPreferences("ScratchPrefs", Context.MODE_PRIVATE)
        val lastScratchDate = sharedPreferences.getString(scratchPrefKey, "")

        // Check if the user has already scratched today
        if (lastScratchDate == currentDate) {
            // Disable scratch view or show a message indicating the card has already been scratched
            scratchView.visibility = View.GONE
            Toast.makeText(requireContext(), "You have already scratched today. Come back tomorrow!", Toast.LENGTH_LONG).show()
        } else {
            // Enable scratch view
            scratchView.setRevealListener(object : ScratchView.IRevealListener {
                override fun onRevealed(scratchView: ScratchView) {
                    Toast.makeText(requireContext(), "Revealed", Toast.LENGTH_LONG).show()

                    // Store the current date as the last scratch date
                    with(sharedPreferences.edit()) {
                        putString(scratchPrefKey, currentDate)
                        apply()
                    }
                }

                override fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float) {
                    if (percent >= 0.5) {
                        Log.d("Reveal Percentage", "onRevealPercentChangedListener: $percent")
                        scratchView.reveal() // Fully reveal the image
                    }
                }
            })
        }

        // Check if the user is authenticated
        if (auth.currentUser != null) {
            // Load banner image from Firebase Storage
            val bannerImageView: ImageView = view.findViewById(R.id.imageView5)
            val bannerImageRef = storageReference.child("deal_of_the_day/deal.jpg") // Update path accordingly
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

        return view
    }

    private fun fetchOffers() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cardList.clear()
                for (offerSnapshot in snapshot.children) {
                    val offer = offerSnapshot.getValue(ReviewCards::class.java)
                    offer?.let { cardList.add(it) }
                }
                newRecyclerView.adapter = ReviewPageAdapter(cardList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch offers: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateCardList() {
        cardList.add(ReviewCards("SAVE & SLAY", R.drawable.tokens_1, "Enjoy your Coupon for saving 100rs. @ ShamaRestraunt only for today."))
        cardList.add(ReviewCards("DEAL OF THE DAY", R.drawable.tokens_2, "Get 50% off on your next purchase. Limited time offer!"))
        cardList.add(ReviewCards("WEEKEND SPECIAL", R.drawable.tokens_3, "Avail a 20% discount on all items this weekend at Cafe Mocha."))
        cardList.add(ReviewCards("FESTIVE OFFER", R.drawable.tokens_4, "Buy one, get one free on selected items. Offer valid till stocks last."))
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
