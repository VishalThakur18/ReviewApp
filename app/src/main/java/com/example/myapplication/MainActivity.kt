package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.FeedFragment
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.ReviewsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: MeowBottomNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.fav_icon))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.setting))
        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                1 -> {
                    replaceFragment(HomeFragment())
                }
                2 -> {
                    replaceFragment(FeedFragment())
                }
                3 -> {
                    replaceFragment(ReviewsFragment())
                }
            }
        }
        replaceFragment(HomeFragment())
        bottomNavigation.show(1)
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}


//        setContentView(binding.root)
//To go on Login page through Explore Button (for testing)
//        binding.exploreButton.setOnClickListener{
//            val intent= Intent(this,Login::class.java)
//            startActivity(intent)
//        }
//        private val binding: ActivityMainBinding by lazy {
//            ActivityMainBinding.inflate(layoutInflater)
//        }


//class MainActivity : AppCompatActivity() {
//    private lateinit var bottomNavigation: MeowBottomNavigation
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        bottomNavigation = findViewById(R.id.bottomNavigation)
//        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.home))
//        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.fav_icon))
//        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.setting))
////        bottomNavigation.show(1, true)
//        bottomNavigation.setOnClickMenuListener {
//            when(it.id){
//                1 -> {
//                    replaceFragment(HomeFragment())
//                }
//                2 -> {
//                    replaceFragment(FeedFragment())
//                }
//                3 -> {
//                    replaceFragment(SettingsFragment())
//                }
//            }
//        }
//        replaceFragment(HomeFragment())
//        bottomNavigation.show(1)
//    }
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.fragmentContainer, fragment)
//            .commit()
//    }
//}