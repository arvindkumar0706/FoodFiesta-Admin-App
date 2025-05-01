package com.example.adminfoodfiesta

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodfiesta.Model.OrderDetails
import com.example.adminfoodfiesta.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completeOrderRef : DatabaseReference
    private var listofOrderItem: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.AddItemBtn.setOnClickListener {
            val intent=Intent(this,AddItemActivity::class.java)
            startActivity(intent)
        }

        binding.allItem.setOnClickListener {
            val intent=Intent(this,AllItemActivity::class.java)
            startActivity(intent)
        }



        binding.profileBtn.setOnClickListener {
            val intent=Intent(this,AdminProfileActivity::class.java)
            startActivity(intent)
        }

        binding.completedOrder.setOnClickListener {
            val intent=Intent(this,CompletedOrdersActivity::class.java)
            startActivity(intent)
        }

        binding.pendingOrderTextView.setOnClickListener {
            val intent=Intent(this,PendingOrderActivity::class.java)
            startActivity(intent)
        }

        binding.LogoutBtn.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        pendingOrders()
        completedOrders()
        WholetimeEarning()

    }

    private fun WholetimeEarning() {
        val listofTotalPay = mutableListOf<Int>()



        completeOrderRef=FirebaseDatabase.getInstance().reference.child("CompletedOrder")

        completeOrderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children){
                    for (orderSnapshot in userSnapshot.children){
                        var completeOrder= orderSnapshot.getValue(OrderDetails::class.java)
                        completeOrder?.totalPrice?.replace("$","")?.toInt()
                            ?.let { i ->
                                listofTotalPay.add(i)

                            }
                    }
                }
                binding.TotalEarning.text=listofTotalPay.sum().toString() + "$"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun completedOrders() {
        var completedOrderRef=database.reference.child("CompletedOrder")
        var completedOrderItemCount=0
        completedOrderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrderItemCount=snapshot.childrenCount.toInt()
                binding.CompletedOrderNum.text=completedOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun pendingOrders() {
        database=FirebaseDatabase.getInstance()
        var pendingOrderRef=database.reference.child("OrderDetails")
        var pendingOrderItemCount=0
        pendingOrderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount=snapshot.childrenCount.toInt()
                binding.PendingOrderNum.text=pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}