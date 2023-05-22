package com.example.wastescanner.AdaptersViewHolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wastescanner.Data.Product
import com.example.wastescanner.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class ProductAdapter(options: FirestoreRecyclerOptions<Product>) :
    FirestoreRecyclerAdapter<Product, ProductAdapter.FirebaseViewHolder>(options) {
    //------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return FirebaseViewHolder(view)
    }
    //------------------------------------------------------------------
    override fun onBindViewHolder(holder: FirebaseViewHolder, position: Int, model: Product) {
        holder.bind(model)
    }
    //------------------------------------------------------------------
    inner class FirebaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productlist_name)
        private val productWasteTypeChip: Chip = itemView.findViewById(R.id.productlist_waste_type)
        private val productWasteWeightText: TextView = itemView.findViewById(R.id.productlist_description)
        //------------------------------------------------------------------
        fun bind(item: Product) {
            productNameTextView.text = item.Name
            when (item.WasteCategory) {
                "Plastic" -> {productWasteTypeChip.setChipBackgroundColorResource(R.color.plastic_chip_color)
                    productWasteTypeChip.text = "Plastikas"}
                "Paper"  -> {productWasteTypeChip.setChipBackgroundColorResource(R.color.paper_chip_color)
                    productWasteTypeChip.text = "Popierius"}
                "Glass" -> {productWasteTypeChip.setChipBackgroundColorResource(R.color.glass_chip_color)
                    productWasteTypeChip.text = "Stiklas"}
                "Metal" -> {productWasteTypeChip.setChipBackgroundColorResource(R.color.plastic_chip_color)
                        productWasteTypeChip.text = "Metalas"}
                "Wood" -> {productWasteTypeChip.setChipBackgroundColorResource(R.color.default_chip_color)
                    productWasteTypeChip.text = "Medis"}
                else -> productWasteTypeChip.setChipBackgroundColorResource(R.color.default_chip_color)
            }
            productWasteWeightText.text = "Pakuotė: ${item.Weight}kg, Produktas: ${item.Description}"
        }
        //------------------------------------------------------------------
    }
    //------------------------------------------------------------------
}
