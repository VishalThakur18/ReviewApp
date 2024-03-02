package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.fragments.HomeFragment

class UserProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val ppic : ImageView = findViewById(R.id.profileOnUser)
        ppic.setOnClickListener {
            showCustomDialog()
        }

        val btn: Button = findViewById(R.id.backtobase)
        btn.setOnClickListener {
            supportFinishAfterTransition()
        }
    }

    private fun showCustomDialog() {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.dialog_profilepicselector, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
        val cross = alertDialog.findViewById<ImageButton>(R.id.cancelID)
        if (cross != null) {
            cross.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}