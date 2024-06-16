package com.example.myapplication.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Login
import com.example.myapplication.R
import com.example.myapplication.UserProfile
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.homeCards
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import homeAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var backgroundId : Array<Int>
    private lateinit var frontgroundId : Array<Int>
    private lateinit var dishImage : Array<Int>
    lateinit var title: Array<String>
    lateinit var description: Array<String>
    private lateinit var newRecyclerView: RecyclerView
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Fetching only the first name from the user's display name
        val displayName = currentUser?.displayName ?: ""
        val firstName = displayName.split(" ").firstOrNull() ?: ""

        // Setting the user name
        binding.userName.text = "Hi $firstName"
        // Setting up the profile picture
        Glide.with(requireContext())
            .load(currentUser?.photoUrl)
            .circleCrop()
            .placeholder(R.drawable.circular_bg)
            .into(binding.profilePic)

        // Click listener for profile picture
        binding.profilePic.setOnClickListener {
            val intent1 = Intent(requireContext(), UserProfile::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                binding.profilePic,
                "photu"
            )
            startActivity(intent1, options.toBundle())
        }



        //Recycler view on home page....................................
        backgroundId = arrayOf(
            R.drawable.homechoice_bg,
            R.drawable.homechoice_bg_blue,
            R.drawable.homechoice_bg_yellow,
            R.drawable.homechoice_bg
        )
        title = arrayOf(
            "Noodles",
            "RiceBowls",
            "Sandwiches",
            "Juices"
        )
        dishImage = arrayOf(
            R.drawable.visphotu,
            R.drawable.visphotu,
            R.drawable.visphotu,
            R.drawable.visphotu
        )
        description = arrayOf(
            "Chinese Chataka",
            "Fullfilling",
            "Tangy & Tasty",
            "Rehy-date"
        )
        frontgroundId = arrayOf(
            R.drawable.untitled,
            R.drawable.untitled,
            R.drawable.untitled,
            R.drawable.untitled
        )
        newRecyclerView = binding.recyclerHome
        newRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)

//        newRecyclerView = view.findViewById(R.id.recyclerHome)
//        newRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val items = mutableListOf<homeCards>()
        for (i in backgroundId.indices) {
            items.add(homeCards(backgroundId[i], dishImage[i], frontgroundId[i], title[i], description[i]))
        }
        val adapter = homeAdapter(items)
        newRecyclerView.adapter = adapter

        //adding review functionaltiy
        val addReviewsImageView: ImageView = binding.root.findViewById(R.id.add_reviews)
        addReviewsImageView.setOnClickListener {
            showBottomSheet(requireContext())
        }

        return binding.root
    }

    private fun showBottomSheet(context: Context) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottomsheethome, null)
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        // Apply slide-in animation
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        bottomSheetView.startAnimation(slideIn)

        // Get the BottomSheetBehavior
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(Color.TRANSPARENT) // Make the bottom sheet background transparent
        }

        // Apply custom background with curved edges to the inflated view
        bottomSheetView.background = ContextCompat.getDrawable(context, R.drawable.dialog_home_bg_newitem)

        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        // Disable drag and set expanded state
        behavior.isDraggable = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Handle dismiss with slide-out animation
        val slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom)
        slideOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                bottomSheetDialog.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        // Set BottomSheetCallback to handle state changes
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetView.startAnimation(slideOut)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        bottomSheetDialog.show()
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


