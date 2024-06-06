package com.example.myapplication.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.feedAdapter
import com.example.myapplication.feedCards


class dishesFragment : Fragment() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<feedCards>
    private lateinit var imageId : Array<Int>
    lateinit var title: Array<String>
    lateinit var price: Array<String>
    lateinit var likes: Array<String>
    lateinit var dislikes: Array<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_dishes, container, false)
        imageId = arrayOf(
            R.drawable.place_holder,
            R.drawable.biryani_plahol,
            R.drawable.burger_plahol,
            R.drawable.pizza_plahol
        )

        title = arrayOf(
            "Food in General",
            "Biryani",
            "Burger",
            "Pizza"
        )

        price = arrayOf(
            "150",
            "110",
            "55",
            "320"
        )
        likes = arrayOf(
            "0 k",
            "32",
            "21",
            "17"
        )
        dislikes = arrayOf(
            "0 k",
            "3",
            "2",
            "1"
        )
        newRecyclerView = view.findViewById(R.id.dishesrecyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newRecyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<feedCards>()

        getUserdata()
        return view
    }



    private fun getUserdata() {
        for(i in imageId.indices) {
            val cards = feedCards(imageId[i],title[i],price[i],likes[i],dislikes[i])
            newArrayList.add(cards)
        }
        newRecyclerView.adapter = feedAdapter(newArrayList)
    }

}