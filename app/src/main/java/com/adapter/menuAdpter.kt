package com.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.food_delivery_system.databinding.MenuItemBinding
import com.example.food_delivery_system.detailsActivity
import com.example.model.MenuItemm

class menuAdpter(
    private val menuItems: List<MenuItemm>,
    private val requireContext:Context):
    RecyclerView.Adapter<menuAdpter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
     val binding =MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
      holder.bind(position)
    }
    override fun getItemCount(): Int = menuItems.size


    inner class MenuViewHolder(private val binding: MenuItemBinding):RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position=adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    openDetailsActivity(position)
                }

            }
        }

        private fun openDetailsActivity(position: Int) {
         val menuItem=menuItems[position]
            val intent=Intent(requireContext,detailsActivity::class.java).apply {
                putExtra("MenuItemName",menuItem.foodName)
                putExtra("MenuItemImage",menuItem.foodImage)
                putExtra("MenuItemDescription",menuItem.foodDescription)
                putExtra("MenuItemIngredients",menuItem.foodIngredient)
                putExtra("MenuItemPrice",menuItem.foodPrice)
            }
            // start the details activity
            requireContext.startActivity(intent)
       }

        // set data in to recyclerview item name namr,price,image
        fun bind(position: Int) {
            val menuItem=menuItems[position]
            binding.apply {
                menufoodname.text=menuItem.foodName
                menuPrice.text=menuItem.foodPrice
                val uri=Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)

            }

        }

    }





}

