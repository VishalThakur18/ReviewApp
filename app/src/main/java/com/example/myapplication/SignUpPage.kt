package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import model.UserModel
class  SignUpPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userName: String
    private lateinit var userNumber: String
    private lateinit var email: String
    private lateinit var password: String
    private val binding: ActivitySignUpPageBinding by lazy {
        ActivitySignUpPageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initializing Firebase Authentication
        auth = FirebaseAuth.getInstance()
        // Initializing Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Password text change listener
        binding.userPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isValidPassword(s.toString())) {
                    changePasswordFieldOutlineColor(R.drawable.edit_text_green_border)
                } else {
                    changePasswordFieldOutlineColor(R.drawable.edit_text_red_border)
                }
            }
        })
        // Set OnFocusChangeListener to change outline color back to original when losing focus
        binding.userPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                changePasswordFieldOutlineColor(R.drawable.button_back_login)
            }
        }

        // Redirecting to the Login page after successful registration
        binding.signUpbutton.setOnClickListener {
            // Get details from User
            userName = binding.userName.text.toString().trim()
            userNumber = binding.userNumber.text.toString().trim()
            email = binding.userEmail.text.toString().trim()
            password = binding.userPassword.text.toString().trim()

            if (userName.isBlank() || email.isBlank() || password.isBlank() || userNumber.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }
        // Redirecting to the Login Page if already have an account
        binding.signIn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        // Highlighting the "Sign In" text
        val textViewed = findViewById<TextView>(R.id.signIn)
        val originalText = "Already have an Account? Sign In"
        val spannableString = SpannableString(originalText)
        val startIndex = originalText.indexOf("Sign In")
        val endIndex = startIndex + "Sign In".length
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.link)),
            startIndex,
            endIndex,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            UnderlineSpan(),
            startIndex,
            endIndex,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        textViewed.text = spannableString
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { displayNameTask ->
                            if (displayNameTask.isSuccessful) {
                                Log.d("SignUpPage", "User display name updated successfully")
                            } else {
                                Log.e("SignUpPage", "Failed to update user display name", displayNameTask.exception)
                            }
                        }

                    user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
                            saveUserData()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        }

                    // If User is Created Successfully then redirect to the login page
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Account Creation Failed 🙁", Toast.LENGTH_SHORT).show()
                    Log.d("Account", "createAccount: Failure", task.exception)
                }
            }
    }
    // To Save the User's registration Data
    private fun saveUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userName = binding.userName.text.toString().trim()
            userNumber = binding.userNumber.text.toString().trim()
            email = binding.userEmail.text.toString().trim()
            password = binding.userPassword.text.toString().trim()
            val user = UserModel(
                name = userName,
                phone = userNumber,
                email = email,
                password = password
            )
            val userId = currentUser.uid
            // Save data in the Firebase
            database.child("users").child(userId).setValue(user)
                .addOnSuccessListener {
                    Log.d("SignUpPage", "User data saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SignUpPage", "Failed to save user data", e)
                }
        } else {
            Log.e("SignUpPage", "User is not authenticated")
            Toast.makeText(this, "User is not authenticated. Data saving failed.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check if the password is valid
    private fun isValidPassword(password: String): Boolean {
        // Password should have at least one letter, one special character, one digit, and a minimum length of 8
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    // Function to change the outline color of the password field
    private fun changePasswordFieldOutlineColor(drawableId: Int) {
        val drawable = ContextCompat.getDrawable(this, drawableId)
        binding.userPassword.background = drawable
    }
}
