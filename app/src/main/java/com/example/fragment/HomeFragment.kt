package com.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.menuAdpter
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.food_delivery_system.MenuBottomSheetFragment
import com.example.food_delivery_system.R
import com.example.food_delivery_system.databinding.FragmentHomeBinding
import com.example.model.MenuItemm
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding :FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var menuItems:MutableList<MenuItemm>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog=MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager,"Test")
        }
        // retrive and display popular menu item
        retriveAndDisplayPopulerItem()
        return binding.root



    }

    private fun retriveAndDisplayPopulerItem() {
        // get reference to the database
        database=FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference=database.reference.child("menu")
        menuItems= mutableListOf()

       // retrive menu item from the database
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(MenuItemm::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                // display random Popular Items
                randomPopularItem()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private fun randomPopularItem() {
        // create as shuffled list of menu items
        val index=menuItems.indices.toList().shuffled()
        val numItemToShow=6
        val subsetMenuItems=index.take(numItemToShow).map { menuItems[it]}
        setPopularItemAdapter(subsetMenuItems)
    }

    private fun setPopularItemAdapter(subsetMenuItems: List<MenuItemm>) {
        val adapter =menuAdpter(subsetMenuItems,requireContext())
        binding.populerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.populerRecyclerView.adapter=adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagelist = ArrayList<SlideModel>()
        imagelist.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner3,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner4,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner5,ScaleTypes.FIT))

        val imageSlider =binding.imageSlider
        imageSlider.setImageList(imagelist)
        imageSlider.setImageList(imagelist,ScaleTypes.FIT)
        imageSlider.setItemClickListener(object :ItemClickListener{
            override fun doubleClick(position: Int) {

            }

            override fun onItemSelected(position: Int) {
                val imagePosition = imagelist[position]
                val itemMessage="Selected Image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }
        })


    }


}