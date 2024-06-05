package com.example.myapplication.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.Login
import com.example.myapplication.R
import com.example.myapplication.UserProfile
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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

        // Click listener for back to login button
        binding.exploreButton.setOnClickListener {
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
