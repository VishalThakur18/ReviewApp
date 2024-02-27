package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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

    //Google Sign Up
    private lateinit var googleSignIn: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Initializing Firebase authentication
        auth= Firebase.auth
        //Initializing database
        database =Firebase.database.reference

       //Google SignIn
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString((R.string.web_client_id)))
            .requestEmail()
            .build()
        googleSignIn=GoogleSignIn.getClient(this,gso)

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
        binding.googleSignIn.setOnClickListener{
            signInWithGoogle()
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

    private fun signInWithGoogle(){
        val signInIntent=googleSignIn.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if (result.resultCode== Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }

    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account:GoogleSignInAccount?=task.result
            if (account!=null){
                updateUIGoogle(account)
            }
        }else{
            Toast.makeText(this,"SignIn Failed , Try Again Later",Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUIGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }


    }


}