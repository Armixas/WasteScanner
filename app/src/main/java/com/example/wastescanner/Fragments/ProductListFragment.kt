package com.example.wastescanner.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wastescanner.AdaptersViewHolders.ProductAdapter
import com.example.wastescanner.Data.Product
import com.example.wastescanner.R
import com.example.wastescanner.databinding.FragmentProductlistBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProductListFragment : Fragment() {
    private lateinit var binding: FragmentProductlistBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    //------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductlistBinding.inflate(inflater, container, false)
        return binding.root
    }
    //------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.product_list)


        recyclerView = binding.productlistRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(getQuery(), Product::class.java)
            .build()
        adapter = ProductAdapter(options)
        recyclerView.adapter = adapter

        adapter.startListening()
    }
    //------------------------------------------------------------------
    private fun getQuery() = FirebaseFirestore.getInstance()
        .collection("Products")
        .orderBy("Name")
        /*.limit(10)*/
    //------------------------------------------------------------------
    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }
    //------------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
    //------------------------------------------------------------------
}
