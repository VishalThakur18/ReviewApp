package com.example.myapplication

import Model.UserModel
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityUserProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

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
                Picasso.get()
                    .load(url)
                    .transform(CropCircleTransformation())
                    .into(binding.profileOnUser)
            }

            // Set user name
            val userName = user.displayName ?: ""
            binding.userNameProfile.text = userName

            // Set user email
            binding.userEmailProfile.text = user.email
        }

        // Fetch and display user's phone number
        val userId = mAuth.currentUser?.uid
        userId?.let { uid ->
            // Fetch user data from the database
            val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(uid)
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    userModel?.let { user ->
                        // Set user phone number
                        binding.userPhoneProfile.text = user.phone
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Log.e("UserProfile", "Error fetching user data: ${error.message}")
                }
            })
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
            .setCancelable(false)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val bounceAnimation = ObjectAnimator.ofFloat(dialogView, "translationY", 0f, -50f, 0f)
        bounceAnimation.duration = 500 // Adjust the duration as needed
        bounceAnimation.start()

        // Fetch and display user's profile picture in the dialog
        val currentUser = mAuth.currentUser
        currentUser?.photoUrl?.let { url ->
            val profilePicDialog = dialogView.findViewById<ImageView>(R.id.userImg_Dialog)
            Picasso.get().load(url).transform(CropCircleTransformation()).into(profilePicDialog)
        }

        alertDialog.show()

        val cross = alertDialog.findViewById<ImageButton>(R.id.cancelID)
        cross?.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}
