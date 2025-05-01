package com.example.adminfoodfiesta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminfoodfiesta.Model.userModel
import com.example.adminfoodfiesta.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var Address: String
    private lateinit var phone: String
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference




        binding.alreadyhavebutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.CreateAccBtn.setOnClickListener {

            userName = binding.editTextTextName.text.toString().trim()
            Address = binding.editTextRestaurantName.text.toString().trim()
            email = binding.editTextTextEmailAddress.text.toString().trim()
            phone = binding.editTextPhoneNumber.text.toString().trim()
            password = binding.editTextTextPassword.text.toString().trim()

            if (userName.isBlank() || Address.isBlank()||email.isBlank()||phone.isBlank()||password.isBlank()){
                Toast.makeText(this,"Please Fill all the Details",Toast.LENGTH_LONG).show()
            }else{
                createAccount(email,password)
            }


        }


    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
                Log.d("Account","createAccount : Failure",task.exception)
            }
        }
    }

    private fun saveUserData() {
        userName = binding.editTextTextName.text.toString().trim()
        Address = binding.editTextRestaurantName.text.toString().trim()
        email = binding.editTextTextEmailAddress.text.toString().trim()
        password = binding.editTextTextPassword.text.toString().trim()
        phone = binding.editTextPhoneNumber.text.toString().trim()
        val user=userModel(userName,Address,email,phone,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid
        database.child("AdminUser").child(userId).setValue(user)
    }
}