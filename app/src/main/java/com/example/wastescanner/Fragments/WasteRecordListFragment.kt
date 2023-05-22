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
import com.example.wastescanner.AdaptersViewHolders.WasteRecordAdapter
import com.example.wastescanner.Data.WasteRecord
import com.example.wastescanner.R
import com.example.wastescanner.databinding.FragmentWasterecordlistBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class WasteRecordListFragment : Fragment() {
    private lateinit var binding: FragmentWasterecordlistBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WasteRecordAdapter
    private var email: String? = null
    //------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWasterecordlistBinding.inflate(inflater, container, false)
        return binding.root
    }
    //------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.wasterecord_list)

        val user = FirebaseAuth.getInstance().currentUser
        email = user?.email

        recyclerView = binding.wasterecordlistRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val options = FirestoreRecyclerOptions.Builder<WasteRecord>()
            .setQuery(getQuery(), WasteRecord::class.java)
            .build()
        adapter = WasteRecordAdapter(options)
        recyclerView.adapter = adapter

        adapter.startListening()
    }
    //------------------------------------------------------------------
    private fun getQuery() = FirebaseFirestore.getInstance()
        .collection("WasteRecords")
        .whereEqualTo("AspNetUserID", email)
        .orderBy("Name")

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
