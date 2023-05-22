package com.example.wastescanner.AdaptersViewHolders

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import com.example.wastescanner.Data.WasteRecord
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wastescanner.Data.Product
import com.example.wastescanner.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

class WasteRecordAdapter(options: FirestoreRecyclerOptions<WasteRecord>) :
    FirestoreRecyclerAdapter<WasteRecord, WasteRecordAdapter.FirebaseViewHolder>(options) {
    //------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wasterecord, parent, false)
        return FirebaseViewHolder(view)
    }
    //------------------------------------------------------------------
    override fun onBindViewHolder(holder: FirebaseViewHolder, position: Int, model: WasteRecord) {
        holder.bind(model)
    }
    //------------------------------------------------------------------
    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e(TAG, e.stackTraceToString())
    }
    //------------------------------------------------------------------
    class FirebaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.wasterecord_name)
        private val countTextView: TextView = itemView.findViewById(R.id.wasterecord_count)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.wasterecord_productinfo)
        //------------------------------------------------------------------
        fun bind(wasteRecord: WasteRecord) {
            nameTextView.text = wasteRecord.Name
            countTextView.text = wasteRecord.Count.toString()

            val productRef = wasteRecord.product

            productRef!!.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val productName = documentSnapshot.getString("Name")
                    val productWeight = documentSnapshot.getDouble("Weight")

                    val productDescription = "$productName ($productWeight kg)"
                    descriptionTextView.text = productDescription
                }
            }
                .addOnFailureListener{
                    Log.d(TAG, "Unable to get product reference")
                }
        }
        //------------------------------------------------------------------
    }
}

