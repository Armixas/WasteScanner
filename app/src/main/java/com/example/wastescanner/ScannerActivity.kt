package com.example.wastescanner

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.wastescanner.Fragments.HomeFragment
import com.example.wastescanner.Fragments.ProductsFragment
import com.example.wastescanner.Fragments.WasteRecordFragment
import com.example.wastescanner.databinding.ActivityScannerBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException

class ScannerActivity : AppCompatActivity() {

    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private lateinit var cameraView: SurfaceView
    private lateinit var barcodeInfo: TextView

    public var continueReading = true
    //------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        barcodeInfo = findViewById(R.id.scanner_info_text)
        cameraView = findViewById(R.id.scanner_surfaceView)
    }
    //------------------------------------------------------------------
    override fun onResume() {
        super.onResume()

        continueReading = true

        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setAutoFocusEnabled(true)
            .setRequestedPreviewSize(1600, 1024)
            .build()

        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ActivityCompat.checkSelfPermission(
                        this@ScannerActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@ScannerActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_REQUEST_CODE
                    )
                    return
                }
                try {
                    cameraSource.start(cameraView.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() > 0 && continueReading) {
                    val barcodeValue = barcodes.valueAt(0).displayValue
                    barcodeInfo.post {
                        continueReading = false

                        barcodeInfo.text = barcodeValue
                        val barcodeInt : Long = barcodeValue.toLong()

                        // Checks in firebase if Products Collection contains such Barcode (number)
                        val productsRef = FirebaseFirestore.getInstance().collection("Products")
                        val query = productsRef.whereEqualTo("Barcode", barcodeInt)

                        query.get()
                            .addOnSuccessListener { documents ->
                                if (documents.documents.isEmpty()) {
                                    // Barcode not found, prompt user to add product
                                    val alertDialog = AlertDialog.Builder(this@ScannerActivity)
                                        .setTitle("Produktas nerastas")
                                        .setMessage("$barcodeInt nebuvo rastas duomenų bazėje. Ar norėtumėte pridėti produktą?")
                                        .setPositiveButton("Taip") { _, _ ->
                                            // User wants to add product, navigate to add product screen
                                            val transaction = supportFragmentManager.beginTransaction()
                                            val fragment = ProductsFragment()

                                            val bundle = Bundle()
                                            bundle.putString("barcodeValue", barcodeValue)
                                            fragment.arguments = bundle

                                            transaction.replace(R.id.fragment_container, fragment)
                                            transaction.addToBackStack("HomeFragment")
                                            transaction.commit()
                                        }
                                        .setNegativeButton("Ne") { _, _ ->
                                            // User doesn't want to add product, resume scanning
                                            // for new products
                                            continueReading = true
                                        }
                                        .create()

                                    alertDialog.show()
                                } else {
                                    val transaction = supportFragmentManager.beginTransaction()
                                    val fragment = WasteRecordFragment()

                                    val productDoc = documents.documents[0]
                                    val productRefPath = productDoc.reference.path

                                    val bundle = Bundle()
                                    bundle.putString("productRef", productRefPath)
                                    fragment.arguments = bundle

                                    transaction.replace(R.id.fragment_container, fragment)
                                    transaction.addToBackStack("HomeFragment")
                                    transaction.commit()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("ScannerActivity", "Error getting documents: $exception")
                            }
                    }
                }
            }
        })
    }
    //------------------------------------------------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    cameraSource.start(cameraView.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this@ScannerActivity,
                    "Camera permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    //------------------------------------------------------------------
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }
    //------------------------------------------------------------------
}