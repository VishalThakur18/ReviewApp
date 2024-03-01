package com.example.myapplication

import Model.UserModel
import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivitySignUpPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpPage : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var userName : String
    private lateinit var userNumber:String
    private lateinit var email : String
    private lateinit var password : String
    private val binding:ActivitySignUpPageBinding by lazy {
        ActivitySignUpPageBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initializing Firebase Authentication
        auth= Firebase.auth
        // Initialing Firebase Database
        database=Firebase.database.reference

        //Redirecting to the Login page after successfully  registration
        binding.signUpbutton.setOnClickListener{
            //get details from User
            userName=binding.userName.text.toString().trim()
            userNumber=binding.userNumber.text.toString().trim()
            email=binding.userEmail.text.toString().trim()
            password=binding.userPassword.text.toString().trim()

            if(userName.isBlank() || email.isBlank() || password.isBlank() || userNumber.isBlank()){
                Toast.makeText(this,"Please fill all the details", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email,password)
            }
        }
        //Redirecting to the Login Page if already have an account
        binding.signIn.setOnClickListener{
            val intent= Intent(this,Login::class.java)
            startActivity(intent)
        }
        // Highlighting the "Sign In " text
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
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
            if(task.isSuccessful){

                auth.currentUser?.sendEmailVerification()
                    ?.addOnSuccessListener {
                        Toast.makeText(this,"Please verify your email",Toast.LENGTH_SHORT).show()
                        saveUserData()
                    }
                    ?.addOnFailureListener{
                        Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
                    }

//              Toast.makeText(this,"Account Created Successfully 👍" ,Toast.LENGTH_SHORT ).show()

                // If User is Created Successfully then redirect it to the login page
                val intent= Intent(this,Login::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Account Creation Failed 🙁",Toast.LENGTH_SHORT).show()
                Log.d("Account","createAccount: Failure",task.exception)
            }
        }

    }
    //To Save the Users registration Data
    private fun saveUserData() {
        userName=binding.userName.text.toString().trim()
        userNumber=binding.userNumber.text.toString().trim()
        email=binding.userEmail.text.toString().trim()
        password=binding.userPassword.text.toString().trim()
        val user=UserModel(userName,userNumber ,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //Save data in the Firebase
        database.child("user").child(userId).setValue(user)

    }
}