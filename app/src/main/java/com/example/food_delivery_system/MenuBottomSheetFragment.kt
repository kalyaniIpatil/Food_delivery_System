package com.example.food_delivery_system

import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.menuAdpter

import com.example.food_delivery_system.databinding.FragmentMenuBottomSheetBinding
import com.example.model.MenuItemm
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheetFragment : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentMenuBottomSheetBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems:MutableList<MenuItemm>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding= FragmentMenuBottomSheetBinding.inflate(inflater,container,false)

         binding.buttoBack.setOnClickListener {
             dismiss()
         }
        retriveMenuItem()



        return binding.root
    }

    private fun retriveMenuItem() {
      database= FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference=database.reference.child("menu")
        menuItems= mutableListOf()
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(MenuItemm::class.java)
                    menuItem?.let {
                        menuItems.add(it)}
                    Log.d("ITEM","onDataChange:Data Recceived")
                }
                // once data recive,set to adapter
                setAdapter()
            }



            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun setAdapter() {
        if (menuItems.isNotEmpty()){
            val adapter=menuAdpter(menuItems,requireContext())
            binding.AllMenuRecyclerView.layoutManager= LinearLayoutManager(requireContext())
            binding.AllMenuRecyclerView.adapter=adapter
            Log.d("ITEM","setAdapter data set")
        }else{
            Log.d("ITEM","setAdapter:data Not set")
        }

    }


    companion object {

    }

}