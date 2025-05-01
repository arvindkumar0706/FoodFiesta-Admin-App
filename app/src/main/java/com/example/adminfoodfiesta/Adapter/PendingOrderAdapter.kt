package com.example.adminfoodfiesta.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodfiesta.R
import com.example.adminfoodfiesta.databinding.PendingOrderItemBinding

class PendingOrderAdapter(
    private val custName: MutableList<String>,
    private val foodImage: MutableList<String>,
    private val foodPrice: MutableList<String>,
    private val quantity: MutableList<Int>,
    private val context: Context,
    private val itemClicked: OnItemClicked,


) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding =
            PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = custName.size

    inner class PendingOrderViewHolder(
        private val binding: PendingOrderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isAccepted = false


        fun bind(position: Int) {
            binding.apply {
                CustomerName.text = custName[position]
                ItemQuantity.text = foodPrice[position]
                var UriString = foodImage[position]
                var uri = Uri.parse(UriString)
                Glide.with(context).load(uri).into(itemImage)

                AcceptBtn.apply {
                    if (!isAccepted) {
                        text = "Accept"
                    } else {
                        text = "Dispatch"


                    }
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Dispatch"
                            isAccepted = true
                            showToast("Order is Accepted")
                            itemClicked.onItemAcceptClickListener(position)
                        } else {
                            custName.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("Order is Dispatched")
                            itemClicked.onItemDispatchClickListener(position)
                        }
                    }
                }
                itemView.setOnClickListener {
                    itemClicked.onItemClickListener(position)
                }

            }

        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }


    }
}