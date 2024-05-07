package com.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.menuAdpter
import com.example.food_delivery_system.databinding.FragmentSearchBinding
import com.example.model.MenuItemm
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: menuAdpter
    private lateinit var database: FirebaseDatabase
    private val orignalMenuItems = mutableListOf<MenuItemm>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        //retrive menu item from database
        retriveMenuItem()

        //setup for search view
        setupSearchView()



        return binding.root
    }

    private fun retriveMenuItem() {
        // get database reference
        database = FirebaseDatabase.getInstance()
        // reference to the Menu node
        val foodReference: DatabaseReference = database.reference.child("menu")
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItemm::class.java)
                    menuItem?.let {
                        orignalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showAllMenu() {
        val filteredMenuItem = ArrayList(orignalMenuItems)
        setAdapter(filteredMenuItem)
    }

    private fun setAdapter(filteredMenuItem: List<MenuItemm>) {
        adapter = menuAdpter(filteredMenuItem, requireContext())
        binding.AllMenuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.AllMenuRecyclerView.adapter = adapter
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true

            }


            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })

    }

    private fun filterMenuItems(query: String) {
        val filterMenuItems = orignalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true

        }
        setAdapter(filterMenuItems)
    }

    companion object {

    }

}



