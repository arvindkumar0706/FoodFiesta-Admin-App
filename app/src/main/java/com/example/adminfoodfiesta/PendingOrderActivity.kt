package com.example.adminfoodfiesta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodfiesta.Adapter.DeliveryAdapter
import com.example.adminfoodfiesta.Adapter.PendingOrderAdapter
import com.example.adminfoodfiesta.Model.OrderDetails
import com.example.adminfoodfiesta.databinding.ActivityPendingOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {

    private lateinit var binding: ActivityPendingOrderBinding
    private var listofName: MutableList<String> = mutableListOf()
    private var listofTotalPrice: MutableList<String> = mutableListOf()
    private var listofImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var quantity: MutableList<Int> = mutableListOf()
    private var listofOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.reference.child("OrderDetails")

        getOrderDetails()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun getOrderDetails() {
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listofOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addDataToListForRecyclerView() {
        for (orderItem in listofOrderItem) {
            orderItem.userName?.let { listofName.add(it) }
            orderItem.totalPrice?.let { listofTotalPrice.add(it) }
            orderItem.FoodQuantities?.forEach { quantity.add(it) }
            orderItem.FoodImages?.filterNot { it.isEmpty() }?.forEach {
                listofImageFirstFoodOrder.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrderAdapter(
            listofName,
            listofImageFirstFoodOrder,
            listofTotalPrice,
            quantity,
            this,
            this
        )
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listofOrderItem[position]
        intent.putExtra("userOrderDetails", userOrderDetails)
        startActivity(intent)

    }

    override fun onItemAcceptClickListener(position: Int) {
        val order = listofOrderItem[position]
        val userId = order.userUid
        val orderKey = order.itemPushKey

        if (userId != null && orderKey != null) {
            val orderRef = database.reference.child("CustomersUser").child(userId)
                .child("BuyHistory").child(orderKey)

            orderRef.child("orderStatus").setValue("Accepted")
//            databaseOrderDetails.child(orderKey).child("orderStatus").setValue("Accepted")
        }
    }


    override fun onItemDispatchClickListener(position: Int) {
        val order = listofOrderItem[position]
        val userId = order.userUid
        val orderKey = order.itemPushKey

        if (userId != null && orderKey != null) {
            val orderRef = database.reference.child("CustomersUser").child(userId)
                .child("BuyHistory").child(orderKey)

            val dispatchedOrderRef = database.reference.child("DispatchedOrder").child(userId).child(orderKey)

            // Update the orderStatus to "Dispatched"
            orderRef.child("orderStatus").setValue("Dispatched")
            databaseOrderDetails.child(orderKey).child("orderStatus").setValue("Dispatched")

            // Get the order details from OrderDetails and move it to DispatchedOrder
            databaseOrderDetails.child(orderKey).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val orderData = snapshot.value
                    dispatchedOrderRef.setValue(orderData).addOnSuccessListener {
                        // ✅ Remove from OrderDetails after successful move
                        deleteThisItemFromOrderDetails(orderKey)
                    }
                }
            }
        }
    }


    private fun deleteThisItemFromOrderDetails(dispatchedItemPushKey: String) {
        val orderDetailsItemRef=database.reference.child("OrderDetails").child(dispatchedItemPushKey)
        orderDetailsItemRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Order is Dispatched", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Order is not Dispatched", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        val userIdofClickedItem = listofOrderItem[position].userUid
        val pushKeyOfClickedItem = listofOrderItem[position].itemPushKey
        val buyHistoryRef = database.reference.child("CustomersUser").child(userIdofClickedItem!!)
            .child("BuyHistory").child(pushKeyOfClickedItem!!)
        buyHistoryRef.child("AcceptedOrder").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("AcceptedOrder").setValue(true)
    }
}