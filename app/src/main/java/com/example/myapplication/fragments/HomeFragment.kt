package com.example.myapplication.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.util.Pair
import com.example.myapplication.Login
import com.example.myapplication.R
import com.example.myapplication.UserProfile


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        val toProfile: ImageView = view.findViewById(R.id.profilePic)

        toProfile.setOnClickListener {
            val intent1 = Intent(requireContext(),UserProfile::class.java)
            val pairs : Array<android.util.Pair<View,String>?> = arrayOfNulls(2)
            pairs[0] = android.util.Pair<View,String>(toProfile,"photu")
            pairs[1] = android.util.Pair<View,String>(toProfile,"photu2")
            val options = ActivityOptions.makeSceneTransitionAnimation(activity,*pairs)
            startActivity(intent1,options.toBundle())
        }
        val backtolgin: Button = view.findViewById(R.id.exploreButton)
        backtolgin.setOnClickListener {
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
        }
        return view
    }


}