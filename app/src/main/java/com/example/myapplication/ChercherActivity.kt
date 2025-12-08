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
//            val fragment = ProductFragment.newInstance(
//                product.name,
//                product.brand,
//                product.store,
//                product.price,
//                product.image ?: ""
//            )
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, fragment)   // remplace l'activité par le fragment
//                .addToBackStack(null)                     // pour revenir en arrière
//                .commit()
//
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
//                // Filtrage en direct si nécessaire
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
//
//                    // Récupération du prix via Open Prices
//                    var price = "N/A"
//                    try {
//                        val code = item.optString("code")
//                        if (code.isNotEmpty()) {
//                            val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$code"
//                            val priceResponse = URL(priceUrl).readText()
//                            val priceJson = JSONObject(priceResponse)
//                            val itemsPrice = priceJson.optJSONArray("items")
//                            if (itemsPrice != null && itemsPrice.length() > 0) {
//                                val firstItem = itemsPrice.getJSONObject(0)
//                                val priceValue = firstItem.optDouble("price", -1.0)
//                                if (priceValue != -1.0) price = "%.2f €".format(priceValue)
//                            }
//                        }
//                    } catch (e: Exception) {
//                        // si erreur, le prix reste "N/A"
//                    }
//
//                    tempList.add(
//                        Product(
//                            name = item.optString("product_name", "Nom inconnu"),
//                            brand = item.optString("brands", "Marque inconnue"),
//                            store = item.optString("stores", "Magasin inconnu"),
//                            price = price,
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


package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
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

        val btnFav = findViewById<ImageButton>(R.id.btnFav)
        binding.btnFav.setOnClickListener {
            val shared = getSharedPreferences("user_session", MODE_PRIVATE)
            val isLogged = shared.getBoolean("isLogged", false)

            if (!isLogged) {
                // Pas connecté → direction ConnexionActivity
                startActivity(Intent(this, ConnexionActivity::class.java))
            } else {
                // Connecté → ouvrir FavFragment
                FavFragment().show(supportFragmentManager, "FavFragment")
            }
        }


        // Config RecyclerView
        adapter = ProductAdapter(productList) { product ->
            // 👉 Ouvrir ProductFragment au lieu d’un toast
            val frag = ProductFragment.newInstance(
                product.name,
                product.brand,
                product.store,     // 👈 magasin correct (lié au moins cher)
                product.price,
                product.image ?: ""
            )

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, frag)   // affiche par-dessus l’activité
                .addToBackStack(null)
                .commit()

        }

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProducts.adapter = adapter

        // Recherche
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) fetchProducts(query)
                return false
            }

            override fun onQueryTextChange(newText: String?) = false
        })
    }

//    private fun fetchProducts(query: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=${query}&search_simple=1&action=process&json=1"
//                val response = URL(url).readText()
//                val json = JSONObject(response)
//                val productsJson = json.getJSONArray("products")
//
//                val tempList = mutableListOf<Product>()
//
//                for (i in 0 until productsJson.length()) {
//                    val item = productsJson.getJSONObject(i)
//
//                    // ⭐ Attributs communs
//                    var price = "N/A"
//                    var storeFromPrices = "Magasin inconnu"
//
//                    // ⭐ Récupération du prix via Open Prices
//                    try {
//                        val code = item.optString("code")
//                        if (code.isNotEmpty()) {
//                            val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$code"
//                            val priceResponse = URL(priceUrl).readText()
//
//                            val priceJson = JSONObject(priceResponse)
//                            val itemsPrice = priceJson.optJSONArray("items")
//
//                            if (itemsPrice != null && itemsPrice.length() > 0) {
//
//                                // ⬇️ On prend le prix le moins cher
//                                var minPrice = Double.MAX_VALUE
//                                var bestStore = ""
//
//                                for (j in 0 until itemsPrice.length()) {
//                                    val p = itemsPrice.getJSONObject(j)
//                                    val pValue = p.optDouble("price", -1.0)
//
//                                    if (pValue != -1.0 && pValue < minPrice) {
//                                        minPrice = pValue
//                                        bestStore = p.optString("store", "")
//                                    }
//                                }
//
//                                if (minPrice < Double.MAX_VALUE) {
//                                    price = "%.2f".format(minPrice)
//                                    if (bestStore.isNotEmpty()) {
//                                        storeFromPrices = bestStore
//                                    }
//                                }
//                            }
//                        }
//                    } catch (_: Exception) {}
//
//                    // ⭐ Ajout du produit
//                    tempList.add(
//                        Product(
//                            name = item.optString("product_name", "Nom inconnu"),
//                            brand = item.optString("brands", "Marque inconnue"),
//                            store = storeFromPrices,     // ⭐ magasin du prix le + bas
//                            price = price,
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
private fun fetchProducts(query: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$query&search_simple=1&action=process&json=1"
            val response = URL(url).readText()
            val json = JSONObject(response)
            val productsJson = json.getJSONArray("products")

            val tempList = mutableListOf<Product>()

            for (i in 0 until productsJson.length()) {
                val item = productsJson.getJSONObject(i)

                val code = item.optString("code")
                var bestStore = "Magasin inconnu"
                var bestPrice = "N/A"

                // ---- Récupération du prix le moins cher ----
                if (code.isNotEmpty()) {
                    try {
                        val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$code"
                        val priceResponse = URL(priceUrl).readText()
                        val priceJson = JSONObject(priceResponse)
                        val itemsPrice = priceJson.optJSONArray("items")

                        if (itemsPrice != null && itemsPrice.length() > 0) {

                            var minPrice = Double.MAX_VALUE
                            var minStore = "Magasin inconnu"

                            for (p in 0 until itemsPrice.length()) {
                                val pItem = itemsPrice.getJSONObject(p)
                                val priceValue = pItem.optDouble("price", -1.0)

                                if (priceValue > 0 && priceValue < minPrice) {
                                    minPrice = priceValue
                                    minStore = pItem.optString("store", "Magasin inconnu")
                                }
                            }

                            if (minPrice < Double.MAX_VALUE) {
                                bestPrice = "%.2f".format(minPrice)
                                bestStore = minStore
                            }
                        }
                    } catch (_: Exception) {}
                }

                tempList.add(
                    Product(
                        name = item.optString("product_name", "Nom inconnu"),
                        brand = item.optString("brands", "Marque inconnue"),
                        store = bestStore,
                        price = bestPrice,
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
