package com.example.food_delivery_system

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class FoodZone : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_zone)
        Handler().postDelayed({
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },400)
    }
}