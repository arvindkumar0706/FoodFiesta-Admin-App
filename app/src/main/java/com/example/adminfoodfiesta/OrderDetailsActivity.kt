package com.example.adminfoodfiesta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodfiesta.Adapter.OrderDetailsAdapter
import com.example.adminfoodfiesta.Model.OrderDetails
import com.example.adminfoodfiesta.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding

    private var userName: String? = null
    private var address: String? = null
    private var phoneNum: String? = null
    private var totalPrice: String? = null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()
    private var foodQuantity: ArrayList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { 
            finish()
        }
        
        getDataFromIntent()

    }

    private fun getDataFromIntent() {
        val recievceOrderDetails=intent.getSerializableExtra("userOrderDetails") as OrderDetails

        recievceOrderDetails?.let {orderDetails ->
            userName=recievceOrderDetails.userName
            foodNames=recievceOrderDetails.FoodNames as ArrayList<String>
            foodImages=recievceOrderDetails.FoodImages as ArrayList<String>
            foodQuantity=recievceOrderDetails.FoodQuantities as ArrayList<Int>
            address=recievceOrderDetails.address
            phoneNum=recievceOrderDetails.phoneNumber
            foodPrices=recievceOrderDetails.FoodPrices as ArrayList<String>
            totalPrice=recievceOrderDetails.totalPrice

            setUserDetails()
            setAdapter()
        }



    }

    private fun setAdapter() {
        binding.OrderDetailsRV.layoutManager=LinearLayoutManager(this)
        val adapter=OrderDetailsAdapter(this,foodNames,foodImages,foodQuantity,foodPrices)
        binding.OrderDetailsRV.adapter=adapter
    }

    private fun setUserDetails() {
        binding.name.text=userName
        binding.address.text=address
        binding.phone.text=phoneNum
        binding.totalprice.text=totalPrice
    }
}