package com.example.myapplication.fragments
import HomeAdapter
import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.HomeCards
import com.example.myapplication.R
import com.example.myapplication.UserProfile
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var newRecyclerView: RecyclerView
    private var foodItem= mutableListOf<HomeCards>()

    private val binding get() = _binding!!

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private var imageUri: Uri? = null
    private var imageUrl: String = ""

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        val currentUser = auth.currentUser

        val displayName = currentUser?.displayName ?: ""
        val firstName = displayName.split(" ").firstOrNull() ?: ""

        binding.userName.text = "Hi $firstName"

        Glide.with(requireContext())
            .load(currentUser?.photoUrl)
            .circleCrop()
            .placeholder(R.drawable.circular_bg)
            .into(binding.profilePic)

        binding.profilePic.setOnClickListener {
            val intent1 = Intent(requireContext(), UserProfile::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                binding.profilePic,
                "picture"
            )
            startActivity(intent1, options.toBundle())
        }

        //val addReviewsImageView: ImageView = binding.root.findViewById(R.id.add_reviews)
        binding.addReviews.setOnClickListener {
            showBottomSheet(requireContext())
        }
        binding.viewReview.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
        }
        binding.restaurantBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_restrauntsFragment)
        }
        binding.offerZoneBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_offersFragment)
        }


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
            }
        }

        cropActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val resultUri = result.data?.let { CropImage.getActivityResult(it).uri }
                resultUri?.let {
                    uploadImageToFirebase(it)
                }
            } else if (result.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.data?.let { CropImage.getActivityResult(it).error }
                Toast.makeText(
                    requireContext(),
                    "Crop failed: ${error?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        // Initialize RecyclerView and Adapter
        newRecyclerView = _binding!!.recyclerHome  // Adjust this line according to your binding
        newRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        homeAdapter=HomeAdapter(foodItem,requireContext())
        newRecyclerView.adapter = homeAdapter

        fetchFoodItems()

        return binding.root
    }
    private fun fetchFoodItems() {
        firestore.collection("dishReview")
            .orderBy("likes", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                foodItem.clear() // Clear the list before adding new data
                for (document in documents) {
                    val review = document.toObject(HomeCards::class.java)
                    // Check if the imageUrl field is not null or empty
                    if (review.imageUrl.isNotEmpty()) {
                        foodItem.add(review)
                    }
                }
                homeAdapter.updateReviews(foodItem)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showBottomSheet(context: Context) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottomsheethome, null)
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        bottomSheetView.startAnimation(slideIn)

        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.setBackgroundColor(Color.TRANSPARENT)

        bottomSheetView.background = ContextCompat.getDrawable(context, R.drawable.dialog_home_bg_newitem)

        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.isDraggable = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom)
        slideOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                bottomSheetDialog.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        val dishNameEditText = bottomSheetView.findViewById<EditText>(R.id.dishName)
        val restaurantNameEditText = bottomSheetView.findViewById<EditText>(R.id.restaurantName)
        val dishPriceEditText = bottomSheetView.findViewById<EditText>(R.id.dishPrice)
        val dishReviewEditText = bottomSheetView.findViewById<EditText>(R.id.dishReview)

        bottomSheetView.findViewById<View>(R.id.cancelBtn).setOnClickListener {
            bottomSheetView.startAnimation(slideOut)
        }

        bottomSheetView.findViewById<View>(R.id.postBtn).setOnClickListener {
            val dishName = dishNameEditText.text.toString().trim()
            val restaurantName = restaurantNameEditText.text.toString().trim()
            val dishPrice = dishPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val dishReview = dishReviewEditText.text.toString().trim()

            if (validateInputs(dishName, restaurantName, dishPrice, dishReview)) {

                    postReview(dishName, restaurantName, dishPrice, dishReview, imageUrl)
                    bottomSheetDialog.dismiss() // Dismiss the dialog after posting

            }
        }

        bottomSheetView.findViewById<View>(R.id.uploadGallery).setOnClickListener {
            openGallery()
        }

        bottomSheetView.findViewById<View>(R.id.uploadCamera).setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        bottomSheetDialog.show()
    }
    private fun validateInputs(
        dishName: String,
        restaurantName: String,
        dishPrice: Double,
        dishReview: String
    ): Boolean {
        if (dishName.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Dish Name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (restaurantName.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Restaurant Name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (dishPrice <= 0.0) {
            Toast.makeText(requireContext(), "Enter Valid Price", Toast.LENGTH_SHORT).show()
            return false
        }
        if (dishReview.isEmpty() || dishReview.length !in 1..100) {
            Toast.makeText(requireContext(), "Enter Review (5 to 100 characters)", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            // Double-check if imageUri is properly initialized
            if (imageUri != null) {
                Log.d("CameraIntent", "Image URI set: $imageUri")
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } else {
                Log.e("CameraIntent", "Error: imageUri is null")
                Toast.makeText(requireContext(), "Error initializing image capture", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("CameraIntent", "Error creating image file")
            Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return try {
            File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
        } catch (ex: IOException) {
            Log.e("CreateFileError", "Error creating file: ${ex.message}")
            null
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_GALLERY -> {
                    data?.data?.let { uri ->
                        startCropActivity(uri)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    imageUri?.let {
                        Log.d("ActivityResult", "Starting crop activity with URI: $imageUri")
                        startCropActivity(it)
                    } ?: run {
                        Log.e("ActivityResult", "Error: Image URI is not initialized")
                        Toast.makeText(requireContext(), "Error: Image URI is not initialized", Toast.LENGTH_SHORT).show()
                    }
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    if (result.isSuccessful) {
                        val croppedImageUri = result.uri
                        Log.d("ActivityResult", "Image cropped successfully: $croppedImageUri")
                        uploadImageToFirebase(croppedImageUri)
                    } else {
                        val error = result.error
                        Log.e("ActivityResult", "Crop failed: ${error.message}")
                        Toast.makeText(requireContext(), "Crop failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Log.d("ActivityResult", "Result not OK, resultCode: $resultCode")
        }
    }



    private fun startCropActivity(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireContext(), this)
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    imageUrl = downloadUri.toString()
                    imageUri = null // Reset imageUri after successful upload
                }?.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to retrieve download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun postReview(
        dishName: String,
        restaurantName: String,
        dishPrice: Double,
        dishReview: String,
        imageUrl: String
    ) {
        val currentUser = auth.currentUser
        val userProfilePic = currentUser?.photoUrl.toString()
        val userName = currentUser?.displayName ?: "Anonymous"
        val reviewRef = firestore.collection("dishReview").document()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val review = hashMapOf(
            "id" to reviewRef.id,
            "dishName" to dishName,
            "restaurantName" to restaurantName,
            "price" to dishPrice,
            "reviewText" to dishReview,
            "rating" to 0,
            "imageUrl" to imageUrl,
            "location" to GeoPoint(0.0, 0.0),
            "userProfilePic" to userProfilePic,
            "userName" to userName,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "userId" to userId
        )

        firestore.collection("dishReview")
            .add(review)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Review posted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to post review: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}