package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nafis.bottomnavigation.NafisBottomNavigation

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: NafisBottomNavigation
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the MeowBottomNavigation
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.add(NafisBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(NafisBottomNavigation.Model(2, R.drawable.fav_icon))
        bottomNavigation.add(NafisBottomNavigation.Model(3, R.drawable.fire_profile))

        // Link the MeowBottomNavigation with the NavController
        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> navController.navigate(R.id.homeFragment)
                2 -> navController.navigate(R.id.feedFragment)
                3 -> navController.navigate(R.id.offersFragment)
            }
        }

        // Set Home as the default selection
        if (savedInstanceState == null) {
            bottomNavigation.show(1)
        }

        // Handle the back stack and bottom navigation selection
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> bottomNavigation.show(1, true)
                R.id.feedFragment -> bottomNavigation.show(2, true)
                R.id.offersFragment -> bottomNavigation.show(3, true)
                else -> bottomNavigation.show(1, true)
            }
        }
    }
}
