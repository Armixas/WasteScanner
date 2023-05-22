package com.example.wastescanner.AdaptersViewHolders
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wastescanner.Data.WasteEnum
import com.example.wastescanner.R

class WasteEnumAdapter(private val wasteList: List<WasteEnum>)
    : RecyclerView.Adapter<WasteEnumAdapter.ViewHolder>(){
    //------------------------------------------------------------------
    private val selectedWasteTypes = mutableListOf<WasteEnum>()
    //------------------------------------------------------------------
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.wasteenum_checkbox)
        val textView: TextView = itemView.findViewById(R.id.wasteenum_textview)
    }
    //------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wastetype, parent, false)
        return ViewHolder(view)
    }
    //------------------------------------------------------------------
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wasteType = wasteList[holder.adapterPosition]
        holder.textView.text = wasteType.lithuanianName

        holder.checkBox.isChecked = selectedWasteTypes.contains(wasteType)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            holder.itemView.post {
                if (isChecked) {
                    selectedWasteTypes.clear()
                    selectedWasteTypes.add(wasteType)
                    notifyDataSetChanged()
                } else {
                    selectedWasteTypes.clear()
                    notifyDataSetChanged()
                }

            }

        }
    }
    //------------------------------------------------------------------
    override fun getItemCount() = wasteList.size
    //------------------------------------------------------------------
    fun getSelectedWasteTypes(): List<WasteEnum> {
        return selectedWasteTypes
    }
    //------------------------------------------------------------------
}