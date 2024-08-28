package com.example.myapplication.fragments

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


class ReviewsFragment : Fragment() {

//    private lateinit var homeAdapter:
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var cardList: ArrayList<ReviewCards>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_reviews, container, false)

        // Initialize RecyclerView
        newRecyclerView = view.findViewById(R.id.recyclerReview)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.setHasFixedSize(true)

        // Initialize cardList and populate it
        cardList = arrayListOf()
        populateCardList()

        // Set the adapter
        newRecyclerView.adapter = ReviewPageAdapter(cardList)
        val scratchView: ScratchView = view.findViewById(R.id.scratchView)
        scratchView.setRevealListener(object : ScratchView.IRevealListener {
            override fun onRevealed(scratchView: ScratchView) {
                // Optionally, handle complete reveal action here
                Toast.makeText(requireContext(), "Revealed", Toast.LENGTH_LONG).show()
            }

            override fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float) {
                if (percent >= 0.5) {
                    Log.d("Reveal Percentage", "onRevealPercentChangedListener: $percent")

                    // Programmatically reveal the entire image
                    scratchView.reveal() // Assuming `reveal()` is a method to fully reveal
                }
            }
        })

        return view
    }

    private fun populateCardList() {
        // Add sample data
        cardList.add(ReviewCards("SAVE & SLAY", R.drawable.tokens_1, "Enjoy your Coupon for saving 100rs. @ ShamaRestraunt only for today."))
        cardList.add(ReviewCards("DEAL OF THE DAY", R.drawable.tokens_2, "Get 50% off on your next purchase. Limited time offer!"))
        cardList.add(ReviewCards("WEEKEND SPECIAL", R.drawable.tokens_3, "Avail a 20% discount on all items this weekend at Cafe Mocha."))
        cardList.add(ReviewCards("FESTIVE OFFER", R.drawable.tokens_4, "Buy one, get one free on selected items. Offer valid till stocks last."))
    }

}