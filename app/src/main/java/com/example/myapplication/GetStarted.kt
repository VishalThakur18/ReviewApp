package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.databinding.ActivityGetStartedBinding
import com.example.myapplication.databinding.ActivityMainBinding

class GetStarted : AppCompatActivity() {
    private val binding: ActivityGetStartedBinding by lazy {
        ActivityGetStartedBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.getstarted.setOnClickListener{
            val intent= Intent(this,Login::class.java)
            startActivity(intent)
        }

    }
}