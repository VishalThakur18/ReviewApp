package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anupkumarpanwar.scratchview.ScratchView
import com.example.myapplication.R
import com.example.myapplication.ReviewCards
import com.example.myapplication.ReviewPageAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReviewsFragment : Fragment() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var cardList: ArrayList<ReviewCards>
    private val scratchPrefKey = "SCRATCH_PREF_KEY"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.offers_reviews, container, false)

        newRecyclerView = view.findViewById(R.id.recyclerReview)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.setHasFixedSize(true)

        cardList = arrayListOf()
        populateCardList()

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

        return view
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
