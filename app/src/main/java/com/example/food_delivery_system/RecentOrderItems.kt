package com.example.food_delivery_system

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.RecentBuyAdapter
import com.example.food_delivery_system.databinding.ActivityRecentOrderItemBinding
import com.example.model.OrderDetails

class RecentOrderItems : AppCompatActivity() {
    private val binding: ActivityRecentOrderItemBinding by lazy {
        ActivityRecentOrderItemBinding.inflate(layoutInflater)
    }
    private var allFoodName: ArrayList<String> = arrayListOf()
    private var allFoodImages: ArrayList<String> = arrayListOf()
    private var allFoodPrices: ArrayList<String> = arrayListOf()
    private var allFoodQuantities: ArrayList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.BackButton.setOnClickListener {
            finish()
        }
        val recentOrderItems =
            intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>
        recentOrderItems?.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                val recentOrderItem = orderDetails[0]

                allFoodName = recentOrderItem.foodNames as ArrayList<String>
                allFoodImages = recentOrderItem.foodImages as ArrayList<String>
                allFoodPrices = recentOrderItem.foodPrices as ArrayList<String>
                allFoodQuantities = recentOrderItem.foodQuantities as ArrayList<Int>
            }

        }
        setAdapter()

    }

    private fun setAdapter() {
        val rv = binding.recentBuyItemRecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        val adapter =
            RecentBuyAdapter(this, allFoodName, allFoodImages, allFoodPrices, allFoodQuantities)

        rv.adapter = adapter
    }

}