package com.example.wastescanner.Fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
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
import com.example.wastescanner.AdaptersViewHolders.WasteEnumAdapter
import com.example.wastescanner.Data.WasteEnum
import com.example.wastescanner.R
import com.example.wastescanner.ScannerActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class ProductsFragment : Fragment() {
    //------------------------------------------------------------------
    private lateinit var createProductButton : MaterialButton
    private lateinit var recyclerView: RecyclerView
    private var wasteList = mutableListOf<WasteEnum>()

    private var barcodeLong: Long = 0
    private lateinit var name: String
    private lateinit var description: String
    private var weight: Double = 0.0
    private lateinit var wastetype: String

    private lateinit var productNameTextView: TextView

    //------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        populateWasteList()
    }
    //------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_products, container, false)
        recyclerView = view.findViewById(R.id.add_product_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = WasteEnumAdapter(wasteList)
        recyclerView.adapter = adapter

        productNameTextView = view?.findViewById(R.id.product_name)!!

        val barcodeValue = arguments?.getString("barcodeValue")
        if(barcodeValue != null){
            productNameTextView.text = "Produkto pav.($barcodeValue)"
            barcodeLong = barcodeValue.toLong()
        }

        return view
    }
    //------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCreateProductListener()
    }
    //------------------------------------------------------------------
    private fun setCreateProductListener(){
        createProductButton = requireView().findViewById(R.id.create_product_btn)
        createProductButton.setOnClickListener {
            setProductVariables()

            createFirebaseProduct()
        }
    }
    //------------------------------------------------------------------
    private fun createFirebaseProduct()
    {
        if(wastetype == "")
        {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Nepasirinkta reikšmė")
            builder.setMessage("Pasirinkite atliekos tipą!")
            builder.setPositiveButton("Gerai") { dialog, _ -> dialog.dismiss();}
            val dialog = builder.create()
            dialog.show()
            return;
        }
        val db = FirebaseFirestore.getInstance()

        val productsCollectionRef = db.collection("Products")

        if(!formIsValid()){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Neteisingi duomenys!")
            builder.setMessage("Užpildykite teksto laukelius.")
            builder.setPositiveButton("Gerai") { dialog, _ ->
                dialog.dismiss();
            }
            val alertDialog = builder.create()
            alertDialog.show()
            return
        }

        val newProductData = hashMapOf(
            "Barcode" to barcodeLong,
            "Description" to description,
            "Name" to name,
            "WasteCategory" to wastetype,
            "Weight" to weight
        )

        productsCollectionRef.add(newProductData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Produktas sukurtas sėkmingai, ID: ${documentReference.id}")

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Operacija sėkminga!")
                builder.setMessage("Produktas sukurtas sėkmingai. Ar norite sukurti šiukšlių įrašą?")
                builder.setPositiveButton("Taip"){dialog, _ ->
                    dialog.dismiss();

                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    val fragment = WasteRecordFragment()

                    val bundle = Bundle()
                    bundle.putString("productRef", documentReference.path)
                    fragment.arguments = bundle

                    transaction.replace(R.id.fragment_container, fragment)
                    transaction.commit()
                }
                builder.setNegativeButton("Ne"){dialog, _ ->
                    dialog.dismiss();
                    activity?.finish()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Klaida")
                builder.setMessage("Įvyko klaida pridedant produktą. Pabandykite vėliau.")
                builder.setPositiveButton("Gerai") { dialog, _ -> dialog.dismiss(); activity?.finish() }

                val dialog = builder.create()
                dialog.show()
            }
    }
    //------------------------------------------------------------------
    private fun formIsValid(): Boolean {
        val nameText = requireView().findViewById<TextInputEditText>(R.id.product_form_name_text)
        val descriptionText = requireView().findViewById<TextInputEditText>(R.id.product_form_description_text)
        val weightText = requireView().findViewById<TextInputEditText>(R.id.product_form_weight_text)

        val name = nameText.text.toString().trim()
        val description = descriptionText.text.toString().trim()
        val weight = weightText.text.toString().trim()

        return name.isNotEmpty() && description.isNotEmpty() && weight.isNotEmpty()
    }
    //------------------------------------------------------------------
    private fun setProductVariables()
    {
        name = requireView().findViewById<TextInputEditText>(R.id.product_form_name_text)?.text.toString()
        description = requireView().findViewById<TextInputEditText>(R.id.product_form_description_text)?.text.toString()
        weight = requireView().findViewById<TextInputEditText>(R.id.product_form_weight_text)?.text.toString().toDoubleOrNull() ?: 0.0
        wastetype = (recyclerView.adapter as WasteEnumAdapter).getSelectedWasteTypes().firstOrNull()?.name ?: ""
    }
    //------------------------------------------------------------------
    private fun populateWasteList() {
        WasteEnum.values().forEach { wasteCat -> wasteList.add(wasteCat) }
    }
    //------------------------------------------------------------------
    override fun onDestroy() {
        super.onDestroy()
        (activity as ScannerActivity).continueReading = true
    }
    //------------------------------------------------------------------
}