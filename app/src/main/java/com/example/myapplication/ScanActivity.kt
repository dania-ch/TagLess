package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
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

        // Vérifie la permission
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

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

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
                                    binding.tvResult.text = "Scanné : $code"
                                }
                                fetchProductInfo(code)
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
                Log.e("ScanActivity", "Erreur lors du démarrage de la caméra", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun fetchProductInfo(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiUrl = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
                val response = URL(apiUrl).readText()
                val json = JSONObject(response)
                val product = json.optJSONObject("product")

                if (product != null) {
                    val name = product.optString("product_name", "Nom inconnu")
                    val brands = product.optString("brands", "Marque inconnue")
                    val stores = product.optString("stores", "Non précisé")
                    val price = estimatePrice(name)

                    withContext(Dispatchers.Main) {
                        binding.tvResult.text = """
                            ✅ Produit : $name
                            🏷️ Marque : $brands
                            🛒 Magasin : $stores
                            💶 Prix estimé : $price €
                        """.trimIndent()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.tvResult.text = "Produit non trouvé dans la base OpenFoodFacts."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvResult.text = "Erreur de récupération : ${e.message}"
                }
            }
        }
    }

    private fun estimatePrice(productName: String): String {
        // ⚠️ Exemple simplifié — tu peux remplacer par une vraie API de prix plus tard.
        val basePrice = (1..10).random() + (0..99).random() / 100.0
        return "%.2f".format(basePrice)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
