package com.example.adminfoodfiesta

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodfiesta.Model.userModel
import com.example.adminfoodfiesta.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adminRef:DatabaseReference
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        adminRef=database.reference.child("AdminUser")


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.SaveBtn.setOnClickListener {
            updateUserData()
        }

        binding.name.isEnabled=false
        binding.address.isEnabled=false
        binding.email.isEnabled=false
        binding.phone.isEnabled=false
        binding.SaveBtn.isEnabled=false



        var isEnable=false
        binding.editBtn.setOnClickListener {
            isEnable = !isEnable
            binding.name.isEnabled=isEnable
            binding.address.isEnabled=isEnable
            binding.email.isEnabled=isEnable
            binding.phone.isEnabled=isEnable
            binding.SaveBtn.isEnabled=isEnable

            if (isEnable){
                binding.name.requestFocus()
            }
        }

        retrieveAdminData()

    }



    private fun retrieveAdminData() {
        val currentUser=auth.currentUser?.uid
        if (currentUser != null){
            val userRef=adminRef.child(currentUser)
            userRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        var adminName=snapshot.child("name").getValue()
                        var adminEmail=snapshot.child("email").getValue()
                        var adminAddress=snapshot.child("address").getValue()
                        var adminPhone=snapshot.child("phone").getValue()
                        var adminPassword=snapshot.child("password").getValue()
                        setDataToTextView(adminName,adminEmail,adminAddress,adminPhone,adminPassword)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }


    }

    private fun setDataToTextView(
        adminName: Any?,
        adminEmail: Any?,
        adminAddress: Any?,
        adminPhone: Any?,
        adminPassword: Any?
    ) {
        binding.name.setText(adminName.toString())
        binding.email.setText(adminEmail.toString())
        binding.address.setText(adminAddress.toString())
        binding.phone.setText(adminPhone.toString())

    }

    private fun updateUserData() {
        val updateName=binding.name.text.toString()
        val updateEmail=binding.email.text.toString()
        val updateAddress=binding.address.text.toString()
        val updatePhone=binding.phone.text.toString()
        val currentUser=auth.currentUser?.uid

        if (currentUser != null){
            val userRef=adminRef.child(currentUser)
            userRef.child("name").setValue(updateName)
            userRef.child("email").setValue(updateEmail)
            userRef.child("phone").setValue(updatePhone)
            userRef.child("address").setValue(updateAddress)
            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            auth.currentUser?.updateEmail(updateEmail)
        }
        else {
            Toast.makeText(this, "Profile Updation Failed", Toast.LENGTH_SHORT).show()
        }
    }
}