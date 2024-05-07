package com.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_delivery_system.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class cartAdapter(
                   private val context: Context,
                   private val cartItems:MutableList<String>,
                   private val cartItemPrice:MutableList<String>,
                   private var cartDescription:MutableList<String>,
                   private var cartImages:MutableList<String>,
                   private val cartQuanity:MutableList<Int>,
                   private var cartIngredient:MutableList<String>

): RecyclerView.Adapter<cartAdapter.cartViewholder>() {
    // Firebase instance
    private val auth = FirebaseAuth.getInstance()

    init {
        // Firebase initilization
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItems.size
        itemQuantities = IntArray(cartItemNumber) { 1 }
        cartItemReference = database.reference.child("user").child(userId).child("CartItems")
    }
     companion object {
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartViewholder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return cartViewholder(binding)
    }
    override fun onBindViewHolder(holder: cartViewholder, position: Int) {
        holder.bind(position)

    }


    override fun getItemCount(): Int = cartItems.size
    // get updated quantity
    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity= mutableListOf<Int>()
        itemQuantity.addAll(cartQuanity)
        return itemQuantity
    }

    inner class cartViewholder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                cartFoodname.text = cartItems[position]
                cartitemprice.text = cartItemPrice[position]
                // load Image using glide
                val uriString = cartImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartImage)

                cartitemquantity.text = quantity.toString()

                minbtn.setOnClickListener {
                    decaseQuantity(position)

                }
                addbtn.setOnClickListener {
                    increaseQuantity(position)

                }
                delbtn.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }


                }
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuanity[position]= itemQuantities[position]
                binding.cartitemquantity.text = itemQuantities[position].toString()

            }
        }

        private fun decaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuanity[position]= itemQuantities[position]
                binding.cartitemquantity.text = itemQuantities[position].toString()
            }

        }

        private fun deleteItem(position: Int) {
            val positionRetrieve = position
            getUniqueKeyPosition(positionRetrieve) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }

            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    cartItems.removeAt(position)
                    cartImages.removeAt(position)
                    cartDescription.removeAt(position)
                    cartQuanity.removeAt(position)
                    cartItemPrice.removeAt(position)
                    cartIngredient.removeAt(position)
                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                    // update Item Quantities
                    itemQuantities =
                        itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed To Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }


        private fun getUniqueKeyPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
            cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == positionRetrieve) {
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }
}