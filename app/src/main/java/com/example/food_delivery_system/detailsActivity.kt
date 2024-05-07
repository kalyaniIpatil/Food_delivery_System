package com.example.food_delivery_system

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.food_delivery_system.databinding.ActivityDetailsBinding
import com.example.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class detailsActivity : AppCompatActivity() {
    private lateinit var binding :ActivityDetailsBinding
    private var foodName:String?=null
    private var foodImage:String?=null
    private var foodDescriptions:String?=null
    private var foodIngredients:String?=null
    private var foodPrice:String?=null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // initialize FirebaseAuth
        auth=FirebaseAuth.getInstance()
        foodName=intent.getStringExtra("MenuItemName")
        foodDescriptions=intent.getStringExtra("MenuItemDescription")
        foodIngredients=intent.getStringExtra("MenuItemIngredients")
        foodPrice=intent.getStringExtra("MenuItemPrice")
        foodImage=intent.getStringExtra("MenuItemImage")
        with(binding){
            detailFoodName.text=foodName
            detailDesccription.text=foodDescriptions
            detailIngrediens.text=foodIngredients
            Glide.with(this@detailsActivity).load(Uri.parse(foodImage)).into(detailFoodImage)
        }
        binding.bImgBtn.setOnClickListener {
            finish()
        }
        binding.addItemButton.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database=FirebaseDatabase.getInstance().reference
        val userId=auth.currentUser?.uid?:""

    //Create a cartItem object
        val cartItem=CartItems(foodName.toString(),foodPrice.toString(),foodDescriptions.toString(),foodImage.toString(), foodQuantity = 1)
        // save data to cart item
        database.child("user").child(userId).child("cartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Items added into cart successFully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Item Not added", Toast.LENGTH_SHORT).show()
        }

    }
}