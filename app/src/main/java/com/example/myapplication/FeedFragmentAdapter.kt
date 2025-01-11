package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.fragments.DishesFragment
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.fragments.RestaurantsFragment

class FeedFragmentAdapter(fa: FeedFragment):FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> DishesFragment()
            1 -> RestaurantsFragment()
            else -> DishesFragment()
        }
    }
}