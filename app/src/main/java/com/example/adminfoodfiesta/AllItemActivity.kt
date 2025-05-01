package com.example.adminfoodfiesta

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodfiesta.Adapter.MenuItemAdapter
import com.example.adminfoodfiesta.Model.AllMenu
import com.example.adminfoodfiesta.databinding.ActivityAllItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class AllItemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems:ArrayList<AllMenu> = ArrayList()

    private lateinit var binding: ActivityAllItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAllItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference=FirebaseDatabase.getInstance().reference
        retrieveMenuItem()



        binding.backBtn.setOnClickListener {
            finish()
        }





    }

    private fun retrieveMenuItem() {
        database=FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference=database.reference.child("Menu")
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for (foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "Error : ${error.message}", )
            }

            private fun setAdapter() {

                val adapter=MenuItemAdapter(this@AllItemActivity,menuItems,databaseReference){position->
                    deleteMenuItem(position)
                }
                binding.AllItemRecyclerView.layoutManager=LinearLayoutManager(this@AllItemActivity)
                binding.AllItemRecyclerView.adapter=adapter
            }
        })
    }

    private fun deleteMenuItem(position: Int) {
        val menuItemToDelete=menuItems[position]
        val menuItemKey=menuItemToDelete.key
        val foodMenuRef=database.reference.child("Menu").child(menuItemKey!!)
        foodMenuRef.removeValue().addOnCompleteListener { task->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.AllItemRecyclerView.adapter?.notifyItemRemoved(position)
            }else{
                Toast.makeText(this, "Item Not Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}