//package com.example.myapplication
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.OptIn
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.content.ContextCompat
//import com.example.myapplication.databinding.ActivityScanBinding
//import com.google.mlkit.vision.barcode.BarcodeScannerOptions
//import com.google.mlkit.vision.barcode.BarcodeScanning
//import com.google.mlkit.vision.barcode.common.Barcode
//import com.google.mlkit.vision.common.InputImage
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//import java.net.URL
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//class ScanActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityScanBinding
//    private lateinit var cameraExecutor: ExecutorService
//
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (granted) startCamera()
//            else binding.tvResult.text = "Permission caméra refusée"
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityScanBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        // Vérifie la permission
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            == PackageManager.PERMISSION_GRANTED
//        ) {
//            startCamera()
//        } else {
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//    @OptIn(ExperimentalGetImage::class)
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder().build()
//            preview.setSurfaceProvider(binding.previewView.surfaceProvider)
//
//            val imageAnalysis = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//
//            val options = BarcodeScannerOptions.Builder()
//                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
//                .build()
//            val scanner = BarcodeScanning.getClient(options)
//
//            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
//                val mediaImage = imageProxy.image
//                if (mediaImage != null) {
//                    val image = InputImage.fromMediaImage(
//                        mediaImage, imageProxy.imageInfo.rotationDegrees
//                    )
//                    scanner.process(image)
//                        .addOnSuccessListener { barcodes ->
//                            if (barcodes.isNotEmpty()) {
//                                val code = barcodes.first().rawValue ?: ""
//                                runOnUiThread {
//                                    binding.tvResult.text = "Scanné : $code"
//                                }
//                                fetchProductInfo(code)
//                            }
//                        }
//                        .addOnCompleteListener { imageProxy.close() }
//                } else imageProxy.close()
//            }
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
//                )
//            } catch (e: Exception) {
//                Log.e("ScanActivity", "Erreur lors du démarrage de la caméra", e)
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun fetchProductInfo(barcode: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val apiUrl = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
//                val response = URL(apiUrl).readText()
//                val json = JSONObject(response)
//                val product = json.optJSONObject("product")
//
//                if (product != null) {
//                    val name = product.optString("product_name", "Nom inconnu")
//                    val brands = product.optString("brands", "Marque inconnue")
//                    val stores = product.optString("stores", "Non précisé")
//                    val price = estimatePrice(name)
//
//                    withContext(Dispatchers.Main) {
//                        binding.tvResult.text = """
//                            ✅ Produit : $name
//                            🏷️ Marque : $brands
//                            🛒 Magasin : $stores
//                            💶 Prix estimé : $price €
//                        """.trimIndent()
//                    }
//                } else {
//                    withContext(Dispatchers.Main) {
//                        binding.tvResult.text = "Produit non trouvé dans la base OpenFoodFacts."
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    binding.tvResult.text = "Erreur de récupération : ${e.message}"
//                }
//            }
//        }
//    }
//
//    private fun estimatePrice(productName: String): String {
//        // ⚠️ Exemple simplifié — tu peux remplacer par une vraie API de prix plus tard.
//        val basePrice = (1..10).random() + (0..99).random() / 100.0
//        return "%.2f".format(basePrice)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }
//}
//
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

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(
                        mediaImage, imageProxy.imageInfo.rotationDegrees
                    )
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                val code = barcodes.first().rawValue ?: ""
                                runOnUiThread {
                                    binding.tvResult.text = "Scanné : $code\nChargement des données..."
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                fetchProductAndPrice(code)
                            }
                        }
                        .addOnCompleteListener { imageProxy.close() }
                } else imageProxy.close()
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("ScanActivity", "Erreur : ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

//    private fun fetchProductAndPrice(barcode: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                // 1️⃣ Récupérer le produit depuis OpenFoodFacts
//                val productUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$barcode.json"
//                val productResponse = URL(productUrl).readText()
//                val productJson = JSONObject(productResponse)
//                val product = productJson.optJSONObject("product")
//
//                // 2️⃣ Récupérer les prix depuis Open Prices
//                val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$barcode.json"
//                val priceResponse = URL(priceUrl).readText()
//                val priceJson = JSONObject(priceResponse)
//                val pricesArr = priceJson.optJSONArray("prices")
//
//                withContext(Dispatchers.Main) {
//                    binding.progressBar.visibility = View.GONE
//
//                    if (product != null) {
//                        val name = product.optString("product_name", "Nom inconnu")
//                        val brand = product.optString("brands", "Marque inconnue")
//
//                        val resultText = StringBuilder()
//                        resultText.append("✅ Produit : $name\n")
//                        resultText.append("🏷️ Marque : $brand\n\n")
//
//                        // 3️⃣ Chercher le prix le plus bas en France
//                        var cheapestPrice: Double? = null
//                        var storeName: String? = null
//                        if (pricesArr != null) {
//                            for (i in 0 until pricesArr.length()) {
//                                val p = pricesArr.getJSONObject(i)
//                                val country = p.optString("country")
//                                val price = p.optDouble("price", Double.MAX_VALUE)
//                                if (country == "FR" && price < (cheapestPrice ?: Double.MAX_VALUE)) {
//                                    cheapestPrice = price
//                                    storeName = p.optString("store", "Magasin inconnu")
//                                }
//                            }
//                        }
//
//                        if (cheapestPrice != null) {
//                            resultText.append("💶 Prix le plus bas en France : ${"%.2f".format(cheapestPrice)} € chez $storeName\n")
//                        } else {
//                            resultText.append("💶 Aucun prix disponible en France pour ce produit.\n")
//                        }
//
//                        binding.tvResult.text = resultText.toString()
//                    } else {
//                        binding.tvResult.text = "Produit non trouvé dans OpenFoodFacts."
//                    }
//                }
//
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    binding.progressBar.visibility = View.GONE
//                    binding.tvResult.text = "Erreur : ${e.message}"
//                }
//            }
//        }
//    }

    private fun fetchProductAndPrice(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1️⃣ Récupérer le produit depuis OpenFoodFacts
                val productUrl = "https://prices.openfoodfacts.org/api/v1/products/code/$barcode"
                val productResponse = URL(productUrl).readText()
                val product = JSONObject(productResponse)
                val details = product.optString("detail")

                // 2️⃣ Récupérer les prix depuis Open Prices
                val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$barcode"
                val priceResponse = URL(priceUrl).readText()
                val priceJson = JSONObject(priceResponse)
                val itemsArray = priceJson.optJSONArray("items")
                val firstItem = itemsArray?.optJSONObject(0)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    if (details == "No Product matches the given query.") {
                        binding.tvResult.text = "Produit non trouvé dans OpenFoodFacts."
                    } else {
                        val name = product.optString("product_name", "Nom inconnu")
                        val brand = product.optString("brands", "Marque inconnue")

                        val resultText = StringBuilder()
                        resultText.append("✅ Produit : $name\n")
                        resultText.append("🏷️ Marque : $brand\n\n")

                        if (firstItem != null) {
                            var store = firstItem?.optJSONObject("location")
                            var storeName = store?.optString("osm_name", "Magasin inconnu")
                            var price = firstItem?.optDouble("price", Double.MAX_VALUE)
                            resultText.append("Magasin : $storeName\n")
                            resultText.append("💶 Prix estimé: ${"%.2f".format(price)} €\n")
                        } else {
                            resultText.append("💶 Aucun prix disponible en France pour ce produit.\n")
                        }
                        binding.tvResult.text = resultText.toString()
                    }
                }

            } catch (e: Exception) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                val stackTraceString = sw.toString()

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvResult.text = "Erreur : ${e.message}\n\nStacktrace:\n$stackTraceString"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}


