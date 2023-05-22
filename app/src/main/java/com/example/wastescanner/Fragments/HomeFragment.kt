package com.example.wastescanner.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.wastescanner.Data.Product
import com.example.wastescanner.Data.WasteRecord
import com.example.wastescanner.R
import com.example.wastescanner.ScannerActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.io.File

class HomeFragment : Fragment() {

    //------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    //------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    //------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setScanButtonListener()
    }
    //------------------------------------------------------------------
    private fun setScanButtonListener(){
        val scanButton = view?.findViewById<Button>(R.id.home_barcode_btn)


        scanButton?.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.commit()
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
        }
    }
    //------------------------------------------------------------------
}