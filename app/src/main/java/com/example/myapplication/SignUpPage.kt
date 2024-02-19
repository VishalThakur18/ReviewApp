package com.example.myapplication

import Model.UserModel
import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            email=binding.userEmail.text.toString().trim()
            password=binding.userPassword.text.toString().trim()

            if(userName.isBlank() || email.isBlank() || password.isBlank()){
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


    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully 👍" ,Toast.LENGTH_SHORT ).show()
                saveUserData()
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
        email=binding.userEmail.text.toString().trim()
        password=binding.userPassword.text.toString().trim()
        val user=UserModel(userName,email,password)
        val UserId = FirebaseAuth.getInstance().currentUser!!.uid
        //Save data in the Firebase
        database.child("user").child(UserId).setValue(user)

    }
}