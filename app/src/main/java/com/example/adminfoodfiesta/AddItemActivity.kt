package com.example.adminfoodfiesta

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodfiesta.Model.AllMenu
import com.example.adminfoodfiesta.databinding.ActivityAddItemBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {

    private lateinit var foodname:String
    private lateinit var foodprice:String
    private lateinit var fooddesciption:String
    private var foodimage: Uri?=null
    private lateinit var foodingredients:String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var binding:ActivityAddItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        binding.AddItembtn.setOnClickListener {
            foodname=binding.foodName.text.toString().trim()
            foodprice=binding.foodPrice.text.toString().trim()
            fooddesciption=binding.shortDescription.text.toString().trim()
            foodingredients=binding.ingredients.text.toString().trim()
//            foodimage=binding.foodselectedImage.text.toString().trim()

            if (!(foodname.isBlank()||foodprice.isBlank()||fooddesciption.isBlank()||foodingredients.isBlank())){
                uploadData()
                Toast.makeText(this, "Item added Successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Fill all Food Item Details", Toast.LENGTH_SHORT).show()
            }

        }

        binding.foodselectedImage.setOnClickListener {
            pickimage.launch("image/*")
        }



        binding.backBtn.setOnClickListener {
            finish()
        }


    }

    private fun uploadData() {
        val menuRef=database.getReference("Menu")
        val newItemKey=menuRef.push().key

        if (foodimage != null){
            val storageRef=FirebaseStorage.getInstance().reference
            val imageRef=storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask=imageRef.putFile(foodimage!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    downloadUrl->
                    val newItem = AllMenu(
                        newItemKey,
                        foodName=foodname,
                        foodPrice = foodprice,
                        foodDescription = fooddesciption,
                        foodIngredients = foodingredients,
                        foodImage = downloadUrl.toString()
                    )
                    newItemKey?.let {
                        key->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "Data Upload Successfully", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "Data Upload Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Image Upload failed", Toast.LENGTH_SHORT).show()
                    }

        }

        }else {
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickimage=registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        if(uri!= null){
            binding.foodselectedImage.setImageURI(uri)
            foodimage=uri
        }

    }
}