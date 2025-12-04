//package com.example.myapplication
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.myapplication.databinding.ActivityChercherBinding
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//import java.net.URL
//
//class ChercherActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityChercherBinding
//    private val productList = mutableListOf<Product>()
//    private lateinit var adapter: ProductAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityChercherBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Config RecyclerView
//        adapter = ProductAdapter(productList) { product ->
//            Toast.makeText(this, "Produit: ${product.name}", Toast.LENGTH_SHORT).show()
//            // Ici tu peux ouvrir ProductFragment si tu veux
//        }
//        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)
//        binding.recyclerViewProducts.adapter = adapter
//
//        // Recherche
//        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (!query.isNullOrEmpty()) {
//                    fetchProducts(query)
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                // Tu peux filtrer en direct si tu veux
//                return false
//            }
//        })
//    }
//
//    private fun fetchProducts(query: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=${query}&search_simple=1&action=process&json=1"
//                val response = URL(url).readText()
//                val json = JSONObject(response)
//                val productsJson = json.getJSONArray("products")
//
//                val tempList = mutableListOf<Product>()
//                for (i in 0 until productsJson.length()) {
//                    val item = productsJson.getJSONObject(i)
//                    tempList.add(
//                        Product(
//                            name = item.optString("product_name", "Nom inconnu"),
//                            brand = item.optString("brands", "Marque inconnue"),
//                            store = item.optString("stores", "Magasin inconnu"),
//                            price = "N/A",
//                            image = item.optString("image_front_small_url", "")
//                        )
//                    )
//                }
//
//                withContext(Dispatchers.Main) {
//                    productList.clear()
//                    productList.addAll(tempList)
//                    adapter.notifyDataSetChanged()
//                }
//
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@ChercherActivity, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//}
//

package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityChercherBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class ChercherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChercherBinding
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChercherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Config RecyclerView
        adapter = ProductAdapter(productList) { product ->
            Toast.makeText(this, "Produit: ${product.name}", Toast.LENGTH_SHORT).show()
            // Ici tu peux ouvrir ProductFragment avec les infos du produit
        }
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProducts.adapter = adapter

        // Recherche
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    fetchProducts(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filtrage en direct si nécessaire
                return false
            }
        })
    }

    private fun fetchProducts(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=${query}&search_simple=1&action=process&json=1"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val productsJson = json.getJSONArray("products")

                val tempList = mutableListOf<Product>()
                for (i in 0 until productsJson.length()) {
                    val item = productsJson.getJSONObject(i)

                    // Récupération du prix via Open Prices
                    var price = "N/A"
                    try {
                        val code = item.optString("code")
                        if (code.isNotEmpty()) {
                            val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$code"
                            val priceResponse = URL(priceUrl).readText()
                            val priceJson = JSONObject(priceResponse)
                            val itemsPrice = priceJson.optJSONArray("items")
                            if (itemsPrice != null && itemsPrice.length() > 0) {
                                val firstItem = itemsPrice.getJSONObject(0)
                                val priceValue = firstItem.optDouble("price", -1.0)
                                if (priceValue != -1.0) price = "%.2f €".format(priceValue)
                            }
                        }
                    } catch (e: Exception) {
                        // si erreur, le prix reste "N/A"
                    }

                    tempList.add(
                        Product(
                            name = item.optString("product_name", "Nom inconnu"),
                            brand = item.optString("brands", "Marque inconnue"),
                            store = item.optString("stores", "Magasin inconnu"),
                            price = price,
                            image = item.optString("image_front_small_url", "")
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    productList.clear()
                    productList.addAll(tempList)
                    adapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChercherActivity, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
