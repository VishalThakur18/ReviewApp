package com.example.myapplication.fragments

import Restaurant
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import android.os.CountDownTimer
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RestaurantAdapter
import java.util.Calendar
import java.util.concurrent.TimeUnit

class RestrauntsFragment : Fragment() {

    private lateinit var daysTextView: TextView
    private lateinit var hoursTextView: TextView
    private lateinit var minutesTextView: TextView
    private lateinit var secondsTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private val restaurantList = mutableListOf<Restaurant>()

    //private val countdownTimeInMillis = 7 * 24 * 60 * 60 * 1000L // x days * counter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restraunts, container, false)

        // Initialize views
        daysTextView = view.findViewById(R.id.daysTextView)
        hoursTextView = view.findViewById(R.id.hoursTextView)
        minutesTextView = view.findViewById(R.id.minutesTextView)
        secondsTextView = view.findViewById(R.id.secondsTextView)

        // Start the countdown timer
        val timeUntilNextSunday = calculateTimeUntilNextSundayMidnight()
        startCountdownTimer(timeUntilNextSunday)


        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.restrauntRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Load example data into the list
        loadRestaurants()

        // Initialize and set adapter
        adapter = RestaurantAdapter(restaurantList)
        recyclerView.adapter = adapter

        return view
    }

    private fun calculateTimeUntilNextSundayMidnight(): Long {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Calculate days until next Sunday
        val daysUntilSunday = if (currentDayOfWeek == Calendar.SUNDAY) 0 else 8 - currentDayOfWeek

        // Set calendar to next Sunday midnight
        calendar.add(Calendar.DAY_OF_YEAR, daysUntilSunday)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)

        // Return the time difference in milliseconds
        return calendar.timeInMillis - System.currentTimeMillis()
    }

    private fun loadRestaurants() {
        // Example data for testing
        restaurantList.add(Restaurant("Restaurant 1", 2450, R.drawable.japanese_restraunt))
        restaurantList.add(Restaurant("Restaurant 2", 1234, R.drawable.japanese_restraunt))
        restaurantList.add(Restaurant("Restaurant 3", 987, R.drawable.japanese_restraunt))
        restaurantList.add(Restaurant("Restaurant 3", 9087, R.drawable.japanese_restraunt))
        restaurantList.add(Restaurant("Restaurant 3", 171, R.drawable.japanese_restraunt))
        // Add more items as needed
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
                // Reset countdown to next Sunday midnight
                val timeUntilNextSunday = calculateTimeUntilNextSundayMidnight()
                startCountdownTimer(timeUntilNextSunday)
            }
        }.start()
    }


}