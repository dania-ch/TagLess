package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityScanBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraExecutor: ExecutorService

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera()
            else binding.tvResult.text = "Permission caméra refusée"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }


    }



    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val scannerOptions = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()

            val scanner = BarcodeScanning.getClient(scannerOptions)

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image

                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(
                        mediaImage, imageProxy.imageInfo.rotationDegrees
                    )

                    scanner.process(image)
                        .addOnSuccessListener { codes ->
                            if (codes.isNotEmpty()) {
                                val barcode = codes.first().rawValue ?: return@addOnSuccessListener

                                runOnUiThread {
                                    binding.tvResult.text = "Scanné : $barcode\nChargement..."
                                    binding.progressBar.visibility = View.VISIBLE
                                }

                                fetchProductAndPrice(barcode)
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("ScanActivity", "Erreur caméra : ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

//    private fun fetchProductAndPrice(barcode: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                // 🔎 API 1 : Info produit (Open Food Facts)
//                val productUrl = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
//                val productResponse = URL(productUrl).readText()
//                val productJson = JSONObject(productResponse)
//                val product = productJson.optJSONObject("product")
//
//                var name = "Nom inconnu"
//                var brand = "Marque inconnue"
//                var imageUrl = ""
//
//                if (product != null) {
//                    name = product.optString("product_name", "Nom inconnu")
//                    brand = product.optString("brands", "Marque inconnue")
//                    imageUrl = product.optString("image_front_small_url", "")
//                }
//
//                // 🔎 API 2 : Prix (Open Prices)
//                val priceUrl =
//                    "https://prices.openfoodfacts.org/api/v1/prices?product_code=$barcode"
//                val priceResponse = URL(priceUrl).readText()
//                val priceJson = JSONObject(priceResponse)
//                val items = priceJson.optJSONArray("items")
//
//                var storeName = "Magasin inconnu"
//                var priceValue = "N/A"
//
//                if (items != null && items.length() > 0) {
//                    val item = items.getJSONObject(0)
//                    val storeObj = item.optJSONObject("location")
//                    storeName = storeObj?.optString("osm_name", "Inconnu") ?: "Inconnu"
//
//                    val price = item.optDouble("price", -1.0)
//                    if (price != -1.0) {
//                        priceValue = "%.2f".format(price)
//                    }
//                }
//
//                // ▶️ Ouvrir le Fragment
//                withContext(Dispatchers.Main) {
//                    binding.progressBar.visibility = View.GONE
//
//                    val frag = ProductFragment.newInstance(
//                        name = name,
//                        brand = brand,
//                        store = storeName,
//                        price = priceValue,
//                        image = imageUrl
//                    )
//
//                    supportFragmentManager.beginTransaction()
//                        .replace(android.R.id.content, frag)
//                        .addToBackStack(null)
//                        .commit()
//                }
//
//            } catch (e: Exception) {
//                val sw = StringWriter()
//                e.printStackTrace(PrintWriter(sw))
//                val stackTrace = sw.toString()
//
//                withContext(Dispatchers.Main) {
//                    binding.progressBar.visibility = View.GONE
//                    binding.tvResult.text = "Erreur : ${e.message}\n$stackTrace"
//                }
//            }
//        }
//    }

    private fun fetchProductAndPrice(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 🔎 API 1 : Info produit (Open Food Facts)
                val productUrl = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
                val productResponse = URL(productUrl).readText()
                val productJson = JSONObject(productResponse)
                val product = productJson.optJSONObject("product")

                var name = "Nom inconnu"
                var brand = "Marque inconnue"
                var imageUrl = ""

                if (product != null) {
                    name = product.optString("product_name", "Nom inconnu")
                    brand = product.optString("brands", "Marque inconnue")
                    imageUrl = product.optString("image_front_small_url", "")
                }

                // 🔎 API 2 : Prix (Open Prices)
                val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$barcode"
                val priceResponse = URL(priceUrl).readText()
                val priceJson = JSONObject(priceResponse)
                val items = priceJson.optJSONArray("items")

                var storeName = "Magasin inconnu"
                var priceValue = "N/A"

                if (items != null && items.length() > 0) {
                    val item = items.getJSONObject(0)

                    // 1️⃣ Essayer plusieurs champs pour le nom du magasin
                    val storeObj = item.optJSONObject("location")
                    storeName = storeObj?.optString("osm_name")
                        ?: item.optString("store")   // certains produits ont ce champ
                                ?: item.optString("shop")    // fallback possible
                                ?: "Magasin inconnu"

                    // 2️⃣ Récupérer le prix
                    val price = item.optDouble("price", -1.0)
                    if (price != -1.0) {
                        priceValue = "%.2f".format(price)
                    }
                }

                // ▶️ Ouvrir le Fragment Product avec les infos récupérées
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    val frag = ProductFragment.newInstance(
                        name = name,
                        brand = brand,
                        store = storeName,
                        price = priceValue,
                        image = imageUrl
                    )

                    supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, frag)
                        .addToBackStack(null)
                        .commit()
                }

            } catch (e: Exception) {
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                val stackTrace = sw.toString()

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvResult.text = "Erreur : ${e.message}\n$stackTrace"
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
