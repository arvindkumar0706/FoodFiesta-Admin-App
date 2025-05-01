package com.example.adminfoodfiesta.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodfiesta.databinding.DeliveryitemBinding

class DeliveryAdapter(
    private val customerName:MutableList<String>,
    private val moneyStatus:MutableList<Boolean>
) :RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DeliveryitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DeliveryViewHolder(binding)
    }



    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerName.size

    inner class DeliveryViewHolder(private val binding: DeliveryitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                CustName.text=customerName[position]
                if (moneyStatus[position]==true){
                    MoneyStatus.text="Received"
                }else{
                    MoneyStatus.text="Not Received"
                }
                val colorMap= mapOf(
                        true to Color.GREEN,false to Color.RED
                )
                MoneyStatus.setTextColor(colorMap[moneyStatus[position]]?:Color.BLACK)
                status.backgroundTintList= ColorStateList.valueOf(colorMap[moneyStatus[position]]?:Color.BLACK)
            }
        }

    }
}