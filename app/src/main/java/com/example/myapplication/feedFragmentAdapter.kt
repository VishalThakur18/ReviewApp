package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.fragments.dishesFragment
import com.example.myapplication.fragments.restrauntsFragment

class feedFragmentAdapter(fa: FeedFragment):FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> dishesFragment()
            1 -> restrauntsFragment()
            else -> dishesFragment()
        }
    }

}