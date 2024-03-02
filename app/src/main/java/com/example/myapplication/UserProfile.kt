package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityUserProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        // Fetch and display user's profile picture and name
        val currentUser = mAuth.currentUser
        currentUser?.let { user ->
            // Set profile picture
            user.photoUrl?.let { url ->
                Picasso.get().load(url).into(binding.profileOnUser)
            }
            // Set user name
            binding.userNameProfile.text = user.displayName
        }

        // Click listener for profile picture
        binding.profileOnUser.setOnClickListener {
            showCustomDialog()
        }

        // Click listener for back button
        binding.backtobase.setOnClickListener {
            supportFinishAfterTransition()
        }

        // Click listener for log out button
        binding.logOutBtn.setOnClickListener {
            mAuth.signOut()

            // Sign out from Google also
            val googleSignInClient = GoogleSignIn.getClient(
                this@UserProfile,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build()
            )
            googleSignInClient.signOut()
            val intent = Intent(this@UserProfile, Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
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
