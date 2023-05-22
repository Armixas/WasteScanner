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
import com.example.wastescanner.R
import com.example.wastescanner.ScannerActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime

class WasteRecordFragment : Fragment() {
    //------------------------------------------------------------------
    private lateinit var productNameTextView: TextView
    private lateinit var createWasteRecordButton: MaterialButton
    //------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    //------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_waste_record, container, false)

        return view
    }
    //------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productNameTextView = requireView().findViewById(R.id.wasterecord_information)!!
        setCreateWasteRecordListener()
    }
    //------------------------------------------------------------------
    private fun setCreateWasteRecordListener(){
        createWasteRecordButton = requireView().findViewById(R.id.create_wasterecord_btn)!!
        createWasteRecordButton.setOnClickListener {

            createFirebaseWasteRecord()
        }
    }
    //------------------------------------------------------------------
    private fun createFirebaseWasteRecord()
    {
        val db = FirebaseFirestore.getInstance()

        val wasteCollectionRef = db.collection("WasteRecords")

        val bundle = arguments
        val productRefPath = bundle?.getString("productRef") ?: ""
        val productRef = FirebaseFirestore.getInstance().document(productRefPath)

        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

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

        val quantity = requireView().findViewById<TextInputEditText>(R.id.wasterecord_form_qty_text)
            ?.text.toString().toInt()
        val name = requireView().findViewById<TextInputEditText>(R.id.wasterecord_form_info_text)
            ?.text.toString()
        val date = LocalDateTime.now()


        val newWasteRecordData = hashMapOf(
            "AspNetUserID" to email,
            "Count" to quantity,
            "CreatedAt" to date,
            "Name" to name,
            "product" to productRef
        )

        wasteCollectionRef.add(newWasteRecordData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Šiukšlių įrašas buvo sėkmingai sukurtas, ID: ${documentReference.id}")

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Operacija sėkminga!")
                builder.setMessage("Šiukšlių įrašas buvo sėkmingai sukurtas!")
                builder.setPositiveButton("Gerai") { dialog, _ ->
                    dialog.dismiss();
                    activity?.finish()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
    //------------------------------------------------------------------
    private fun formIsValid(): Boolean {
        val infoText = requireView().findViewById<TextInputEditText>(R.id.wasterecord_form_info_text)
        val qtyText = requireView().findViewById<TextInputEditText>(R.id.wasterecord_form_qty_text)

        val info = infoText.text.toString().trim()
        val qty = qtyText.text.toString().trim()

        return info.isNotEmpty() && qty.isNotEmpty()
    }
    //------------------------------------------------------------------
    override fun onDestroy() {
        super.onDestroy()
        val scannerActivity = requireActivity() as ScannerActivity
        if (!isProductFragmentBehind()) {
            scannerActivity.continueReading = true
        }
    }
    //------------------------------------------------------------------
    private fun isProductFragmentBehind(): Boolean {
        val activity = requireActivity()
        val fragmentManager = activity.supportFragmentManager
        val backStackEntryCount = fragmentManager.backStackEntryCount

        for (i in 0 until backStackEntryCount) {
            val fragmentName = fragmentManager.getBackStackEntryAt(i).name
            if (fragmentName == "ProductsFragment") {
                return true
            }
        }

        return false
    }
    //------------------------------------------------------------------
}