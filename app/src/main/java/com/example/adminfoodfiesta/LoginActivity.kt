package com.example.adminfoodfiesta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminfoodfiesta.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: SignInClient
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        signInClient = Identity.getSignInClient(this)

        val clientId="270094502602-01tho7ohhgvp3fj47nvnm4p47uj5lohv.apps.googleusercontent.com"
        // Request Google Sign-In
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(clientId)
                    .setFilterByAuthorizedAccounts(false) // Allow new accounts
                    .build()
            )
            .build()

        val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                                updateUi(auth.currentUser)
                            } else {
                                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                                Log.e("LoginActivity", "Google sign-in failed", task.exception)
                            }
                        }
                }
            }
        }

        // Navigate to Sign-Up Page
        binding.donthavebutton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        // Email & Password Login
        binding.loginButton.setOnClickListener {
            email = binding.editTextTextEmailAddress.text.toString().trim()
            password = binding.editTextTextPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        // Google Sign-In Button
        binding.googleBtn.setOnClickListener {
            signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    signInLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Google Sign-In Error", e)
                }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUi(auth.currentUser)
                Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Account Not Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateUi(user: FirebaseUser?) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()

    }
}
