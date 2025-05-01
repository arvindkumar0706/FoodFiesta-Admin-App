package com.example.adminfoodfiesta.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodfiesta.Model.AllMenu
import com.example.adminfoodfiesta.databinding.ItemItemBinding
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    private val databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {

    private val itemQuantities = IntArray(menuList.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val quantity = itemQuantities[position]
            val menuItem = menuList[position]
            val uriString = menuItem.foodImage
            val uri = Uri.parse(uriString)

            binding.ItemName.text = menuItem.foodName
            binding.ItemPrice.text = "$ ${menuItem.foodPrice}"
            Glide.with(context).load(uri).into(binding.itemImage)
            binding.itemSizeText.text = quantity.toString()

            // Handle Minus Button
            binding.minusBtn.setOnClickListener {
                val key = menuItem.key
                if (key != null) {
                    val itemRef = databaseReference.child("Menu").child(key).child("quantity")
                    itemRef.get().addOnSuccessListener { snapshot ->
                        val currentQty = snapshot.getValue(Int::class.java) ?: 0
                        val newQty = if (currentQty > 0) currentQty - 1 else 0
                        itemRef.setValue(newQty).addOnCompleteListener {
                            if (it.isSuccessful) {
                                decreaseQuantity(position)
                            }
                        }
                    }
                }
            }

            // Handle Plus Button
            binding.plusBtn.setOnClickListener {
                val key = menuItem.key
                if (key != null) {
                    val itemRef = databaseReference.child("Menu").child(key).child("quantity")
                    itemRef.get().addOnSuccessListener { snapshot ->
                        val currentQty = snapshot.getValue(Int::class.java) ?: 0
                        val newQty = currentQty + 1
                        itemRef.setValue(newQty).addOnCompleteListener {
                            if (it.isSuccessful) {
                                increaseQuantity(position)
                            }
                        }
                    }
                }
            }

            // Handle Delete Button
            binding.DeleteBtn.setOnClickListener {
                onDeleteClickListener(position)
            }
        }

        // Increase Quantity
        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 30) {
                itemQuantities[position]++
                binding.itemSizeText.text = itemQuantities[position].toString()
            }
        }

        // Decrease Quantity
        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                binding.itemSizeText.text = itemQuantities[position].toString()
            }
        }
    }
}
