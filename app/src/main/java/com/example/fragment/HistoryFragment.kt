package com.example.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.BuyAgainAdapter
import com.bumptech.glide.Glide
import com.example.food_delivery_system.RecentOrderItems
import com.example.food_delivery_system.databinding.FragmentHistoryBinding
import com.example.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        //initialize firebase auth
        auth = FirebaseAuth.getInstance()

        //initialize firebase database
        database = FirebaseDatabase.getInstance()

        //Retrive and display the User Order History
        retriveBuyHistory()

        // recent buy Button click
        binding.recentbuyitem.setOnClickListener {
            seeItemsRecentBuy()
        }
        binding.receivedButton.setOnClickListener {
            updateOrderStatus()
        }
        return binding.root

    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val completedOrderReference=database.reference.child("CompletedOrder").child(itemPushKey!!)
        completedOrderReference.child("paymentReceived").setValue(true)
    }
    //function to see items recent buy

    private fun seeItemsRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem", listOfOrderItem)
            startActivity(intent)
        }
    }
    //function to retriv items buy history
        private fun retriveBuyHistory() {
        userId = auth.currentUser?.uid ?: ""
        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapShot in snapshot.children) {
                    val buyHistoryItem = buySnapShot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
                    // display the most recent order details
                    setDataInRecentBuyItem()
                    // setup  to recyclerView with previous orderer details
                    setPreviousBuyItemsRecyclerView()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    //  function to display the most recent order details
    private fun setDataInRecentBuyItem() {
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainFoodName.text = it.foodNames?.firstOrNull() ?: ""
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull() ?: ""
                val image = it.foodImages?.firstOrNull() ?: ""
                Glide.with(requireContext()).load(image).into(buyAgainFoodImage)
                val isOrderIsAccepted=listOfOrderItem[0].orderAccepted
                if (isOrderIsAccepted){
                    orderedStutus.background.setTint(Color.GREEN)
                    receivedButton.visibility=View.VISIBLE
                }

            }
        }
    }

    // fun to setup  to recyclerView with previous orderer details
    private fun setPreviousBuyItemsRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for (i in 1 until listOfOrderItem.size) {
            listOfOrderItem[i].foodNames?.firstOrNull()?.let {
                buyAgainFoodName.add(it)
                listOfOrderItem[i].foodPrices?.firstOrNull()?.let {
                    buyAgainFoodPrice.add(it)
                    listOfOrderItem[i].foodImages?.firstOrNull()?.let {
                        buyAgainFoodImage.add(it)

                    }
                }
            }
        }

             val rv = binding.BuyAgainRecyclerView
                rv.layoutManager = LinearLayoutManager(requireContext())
                buyAgainAdapter =
                    BuyAgainAdapter(
                        buyAgainFoodName,
                        buyAgainFoodPrice,
                        buyAgainFoodImage,
                        requireContext()
                    )
                rv.adapter = buyAgainAdapter
            }
        }

