package com.example.food_delivery_system

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.food_delivery_system.databinding.ActivityLoginBinding
import com.example.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private var username:String ?=null
    private lateinit var email: String
    private lateinit var password:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var GoogleSignInClient:GoogleSignInClient

    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.defualt_web_client_id)).requestEmail().build()
        // Initialization of Firebase Auth
        auth=Firebase.auth
        // Initialization of Firebase Database
        database=Firebase.database.reference
        // Initialization of Google
        GoogleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)

        // login with email and password
        binding.LoginBtn.setOnClickListener {
            // get data from field
            email=binding.email.text.toString().trim()
            password=binding.password.text.toString().trim()
            if (email.isBlank()||password.isBlank()){
                Toast.makeText(this, "Please Enter All the details", Toast.LENGTH_SHORT).show()
            }else{
                createUser()
                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
            }
        }
        binding.donthavebutton.setOnClickListener {
            val intent=Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }
        // google sign-In
        binding.GoogleButton.setOnClickListener {
            val signIntent=GoogleSignInClient.signInIntent
            launcher.launch(signIntent)

        }
    }
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign In Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {

                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUser() {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            task->
            if (task.isSuccessful){
                val user=auth .currentUser
                updateUi(user)

            }else{
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        saveUserdata()
                        val user=auth .currentUser
                        updateUi(user)
                    }else{
                        Toast.makeText(this, "Sign-in-field", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    private fun saveUserdata() {
        email=binding.email.text.toString().trim()
        password=binding.password.text.toString().trim()

        val user=UserModel(username,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid
        // save data into database
        database.child("user").child(userId).setValue(user)

    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if (currentUser!=null){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private fun updateUi(user: FirebaseUser?) {
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}