package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.FeedFragmentAdapter
import com.example.myapplication.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FeedFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        // Initialize TabLayout and ViewPager2
        tabLayout = view.findViewById(R.id.tablout)
        viewPager2 = view.findViewById(R.id.pager_feed)

        // Set up the ViewPager2 adapter
        viewPager2.adapter = FeedFragmentAdapter(this)

        // Set up the TabLayout with custom fonts
        setupTabLayoutWithCustomFont(tabLayout, viewPager2)

        return view
    }

    // Custom function to set up TabLayout with custom fonts
    private fun setupTabLayoutWithCustomFont(tabLayout: TabLayout, viewPager: ViewPager2) {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Set the custom view for each tab
            tab.customView = createCustomTabView(tabLayout.context, getTabTitle(position))
        }.attach()

        // Manually set the text color for the initially selected tab
        val firstTab = tabLayout.getTabAt(0)
        val customView = firstTab?.customView as? TextView
        customView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Add a listener to update colors when tabs are selected or unselected
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val customView = tab.customView as? TextView
                customView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val customView = tab.customView as? TextView
                customView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Optional: Handle reselection if needed
            }
        })
    }


    // Helper function to create a custom tab view
    private fun createCustomTabView(context: Context, title: String): View {
        val tabView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null) as TextView
        tabView.text = title
        // Set default text color for unselected tabs
        tabView.setTextColor(ContextCompat.getColor(context, R.color.black))
        return tabView
    }
    // Helper function to get tab titles
    private fun getTabTitle(position: Int): String {
        return when (position) {
            0 -> "Dishes"
            1 -> "Restaurants"
            else -> "Tab ${position + 1}"
        }
    }
}
