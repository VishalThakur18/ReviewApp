package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.fragments.HomeFragment

class UserProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val btn: Button = findViewById(R.id.backtobase)
        btn.setOnClickListener {
            val intent =Intent(this,HomeFragment::class.java)
            startActivity(intent)
        }
    }
}