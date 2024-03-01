package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.ActivityGetStartedBinding
import com.google.firebase.database.core.Context
import java.text.FieldPosition

class GetStarted : AppCompatActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorContainer: LinearLayout

    private val binding: ActivityGetStartedBinding by lazy {
        ActivityGetStartedBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)

        binding.buttongs.setOnClickListener{
            val scaleAnimation: Animation = AnimationUtils.loadAnimation(this,R.anim.bounce)
            scaleAnimation.setAnimationListener(object :Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@GetStarted,Login::class.java)
                        startActivity(intent)

                    },50)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            binding.buttongs.startAnimation(scaleAnimation)
//            previous one used for intent travel
//            binding.buttongs.setOnClickListener{
//                val scaleAnimation: Animation = AnimationUtils.loadAnimation(this,R.anim.bounce)
//                binding.buttongs.startAnimation(scaleAnimation)
//                val intent= Intent(this,Login::class.java)
//                startActivity(intent)
//            }
        }

    }
    //inputs list of viewpager2 in getstarted
    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.community_gs,
                    title = "Dependable Community",
                    description = "Be a part of the conversation! Engage with the community by commenting on reviews and sharing your thoughts."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.authentic_gs_2,
                    title = "Royal Reviews",
                    description = "Explore the power of user opinions! Dive into a sea of reviews and ratings submitted by our vibrant community. "
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.ratings_gs,
                    title = "Profound Searches",
                    description = "Find what you're looking for effortlessly. Our advanced search and filtering capabilities help you pinpoint reviews for specific products, categories, or topics."
                ) ,
                OnboardingItem(
                    onboardingImage = R.drawable.discount_gs,
                    title = "Hot Offers",
                    description = "Unleash a world of exclusive offers tailored just for you. As a valued member of FoodieMoodie, enjoy access to special promotions, discounts, and limited-time deals."
                )
            )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter =onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
    }

    // indicators on top left corner

    private fun setupIndicators() {
        indicatorContainer = findViewById(R.id.indicatorsContainer)
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for(i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.gs_indicator_inactive_bg
                    )
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }
    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorContainer.childCount
        for(i in 0 until childCount) {
            val imageView =indicatorContainer.getChildAt(i) as ImageView
            if(i== position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.gs_indicator_active_bg
                    )
                )
            }
            else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.gs_indicator_inactive_bg
                    )
                )
            }
        }
    }
}