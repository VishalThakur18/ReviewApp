package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class Login : AppCompatActivity() {

    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference

    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //Initializing Firebase authentication
        auth= Firebase.auth
        //Initializing database
        database =Firebase.database.reference



        binding.loginButton.setOnClickListener {
            //Getting login credentials from the user
            email = binding.userEmail.text.toString().trim()
            password = binding.userPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill the details", Toast.LENGTH_SHORT).show()
            } else {
                createUSerAccount(email, password)

            }
        }

            binding.signUp.setOnClickListener{
            //To go on SignUp page through SignUp
            val intent=Intent(this,SignUpPage::class.java)
            startActivity(intent)
        }

        val textViewed = findViewById<TextView>(R.id.signUp)
        val originalText = "Don't have an Account? Sign Up"
        val spannableString = SpannableString(originalText)
        val startIndex = originalText.indexOf("Sign Up")
        val endIndex = startIndex + "Sign Up".length
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

    private fun createUSerAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task->
            if(task.isSuccessful){
               val verification= auth.currentUser?.isEmailVerified
                if(verification==true) {
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                }else{
                    Toast.makeText(this,"Verify your Email before Login",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Account Not Found",Toast.LENGTH_SHORT).show()

            }

        }

    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


}