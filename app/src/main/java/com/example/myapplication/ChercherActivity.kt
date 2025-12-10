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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                // Pas connecté -> direction ConnexionActivity
                startActivity(Intent(this, ConnexionActivity::class.java))
            } else {
                // Connecté -> ouvrir FavFragment
                FavFragment().show(supportFragmentManager, "FavFragment")
            }
        }


        // Config RecyclerView
        adapter = ProductAdapter(productList) { product ->
            // Ouvrir ProductFragment au lieu d’un toast
            val frag = ProductFragment.newInstance(
                product.name,
                product.brand,
                product.store,
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


//    Fetch pour recuperer les donnes de l'api
    private fun fetchProducts(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chercher produit (Global)
                val searchUrl = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$query&search_simple=1&action=process&json=1&page_size=15"
                val searchResponse = URL(searchUrl).readText()
                val searchJson = JSONObject(searchResponse)
                val productsJson = searchJson.optJSONArray("products") ?: org.json.JSONArray()

                // Prix
                val tasks = (0 until productsJson.length()).map { i ->
                    async {
                        val item = productsJson.getJSONObject(i)
                        val code = item.optString("code")
                        val name = item.optString("product_name", "Nom inconnu")
                        val brand = item.optString("brands", "Marque inconnue")
                        val image = item.optString("image_front_small_url", "")

                        var displayPrice = "N/A"
                        var displayStore = item.optString("stores", "Magasin inconnu")

                        if (code.isNotEmpty()) {
                            try {
                                // FILTER 1: Add '&country=FR' to only get prices in France
                                val priceUrl = "https://prices.openfoodfacts.org/api/v1/prices?product_code=$code&country=FR"
                                val priceResponse = URL(priceUrl).readText()
                                val priceJson = JSONObject(priceResponse)
                                val itemsPrice = priceJson.optJSONArray("items")

                                if (itemsPrice != null && itemsPrice.length() > 0) {
                                    var minPrice = Double.MAX_VALUE
                                    var bestCurrency = "EUR"

                                    for (j in 0 until itemsPrice.length()) {
                                        val pObj = itemsPrice.getJSONObject(j)
                                        val pVal = pObj.optDouble("price", -1.0)
                                        val pCurr = pObj.optString("currency", "EUR")

                                        // 🔹 FILTER 2: Prioritize EUR currency
                                        // We check if it's a valid price AND if it matches our target currency (optional but safer)
                                        if (pVal > 0 && pVal < minPrice && pCurr == "EUR") {
                                            minPrice = pVal
                                            bestCurrency = pCurr
//                                        displayStore = pObj.optString("store_name", "Magasin inconnu")
                                        }
                                    }

                                    // If we found a valid EUR price
                                    if (minPrice != Double.MAX_VALUE) {
                                        displayPrice = "%.2f %s".format(minPrice, bestCurrency)
                                    }
                                }
                            } catch (e: Exception) {
                                // Ignore error for this specific product
                            }
                        }

                        Product(name, brand, displayStore, displayPrice, image)
                    }
                }

                val results = tasks.awaitAll()

                withContext(Dispatchers.Main) {
                    productList.clear()
                    productList.addAll(results)
                    adapter.notifyDataSetChanged()

                    if (results.isEmpty()) {
                        Toast.makeText(this@ChercherActivity, "Aucun produit trouvé.", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChercherActivity, "Erreur: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}