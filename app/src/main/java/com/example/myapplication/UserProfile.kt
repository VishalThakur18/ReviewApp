package com.example.myapplication
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityUserProfileBinding
import com.foysaldev.cropper.CropImage
import com.foysaldev.cropper.CropImageView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import model.UserModel

class UserProfile : AppCompatActivity() {

    companion object {
        const val REQUEST_IMAGE_GALLERY = 2
    }

    private lateinit var selectedImageUri: Uri
    private lateinit var binding: ActivityUserProfileBinding
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val whatsappBtn = findViewById<ImageView>(R.id.whatsappBtn)
//        val facebookBtn = findViewById<LinearLayout>(R.id.facebookBtn)
//        val youtubeBtn = findViewById<ImageView>(R.id.youtubeBtn)
//        val instagramBtn = findViewById<LinearLayout>(R.id.instagramBtn)

        // Set click listeners
//        whatsappBtn.setOnClickListener { openSocialMedia("https://wa.me/<9828270431>") }  // Replace with actual WhatsApp link
//        facebookBtn.setOnClickListener { openSocialMedia("https://www.facebook.com/profile.php?id=100078137621512") }
//        youtubeBtn.setOnClickListener { openSocialMedia("https://youtube.com/shorts/-uENEW-qdOg?si=ImcHbovpYzndkc6P") }
//        instagramBtn.setOnClickListener { openSocialMedia("https://www.instagram.com/therock/?hl=en") }

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

            // Fetch and display user's phone number
            val userId = user.uid
            val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    userModel?.let { user ->
                        // Set user phone number
                        binding.userPhoneProfile.text = user.phone
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserProfile, "Failed to load phone number", Toast.LENGTH_SHORT).show()
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
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this@UserProfile, Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun openSocialMedia(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showCustomDialog() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_GALLERY -> {
                    data?.data?.let { imageUri ->
                        selectedImageUri = imageUri
                        startCropActivity(imageUri)
                    }
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    if (result.error != null) {
                        val error = result.error
                        // Handle crop error
                        Toast.makeText(this, "Crop error: ${error.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        result.uri?.let { uri ->
                            selectedImageUri = uri // Save the cropped image URI

                            // Upload the image to Firebase Storage
                            uploadImageToFirebase(selectedImageUri)
                        }
                    }
                }
            }
        }
    }

    private fun startCropActivity(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(this)
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("profile_pictures/${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        val uploadTask = imagesRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            imagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                // Update the profile picture in Firebase Authentication
                val currentUser = FirebaseAuth.getInstance().currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .build()

                currentUser?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Profile picture updated successfully
                            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()

                            // Update the profile picture in the UI
                            Picasso.get().load(downloadUri)
                                .transform(CropCircleTransformation())
                                .into(binding.profileOnUser)
                        } else {
                            // Profile picture update failed
                            Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }
}
