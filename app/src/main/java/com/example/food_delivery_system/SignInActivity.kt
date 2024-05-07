package com.example.food_delivery_system

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.food_delivery_system.R.*
import com.example.food_delivery_system.databinding.ActivitySignInBinding
import com.example.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignInActivity : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var username:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var googleSignInClint:GoogleSignInClient

    private val binding:ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
       val googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken(getString(R.string.defualt_web_client_id)).requestEmail().build()

        // ininalize Firebase auth
        auth= Firebase.auth

        // intialize Firebase Database
        database=Firebase.database.reference
        // ininalize Firebase auth
        googleSignInClint= GoogleSignIn.getClient(this,googleSignInOptions)

        binding.CreateAccBtn.setOnClickListener {
            username=binding.userName.text.toString()
            email=binding.emailAddress.text.toString().trim()
            password=binding.password.text.toString().trim()
            if(email.isEmpty()|| password.isBlank()||username.isBlank()){
                Toast.makeText(this, "Please Fill all the details", Toast.LENGTH_SHORT).show()
            }else{
                creatAccount(email,password)
            }
        }

        binding.alreadyhavebutton.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.GoogleButton.setOnClickListener {
            val signIntent=googleSignInClint.signInIntent
            launcher.launch(signIntent)

        }
    }
    // launcher for google sign in option
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if (result.resultCode==Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account:GoogleSignInAccount?=task.result
                val credential=GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    task->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Sign In Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{

                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun creatAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account","createAccount:Failure",task.exception)
            }
        }
    }

    private fun saveUserData() {
        // retrieve data from input filed

        username=binding.userName.text.toString()
        password=binding.password.text.toString().trim()
        email=binding.emailAddress.text.toString().trim()

        val user=UserModel(username,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid
         // save data to Firebase Database
        database.child("user").child(userId).setValue(user)
    }
}