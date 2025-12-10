package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import android.content.Context

import android.widget.BaseAdapter
import android.widget.TextView


class FavFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var lvLists: ListView
    private lateinit var btnAddList: ImageButton

    private val favLists = mutableListOf<FavList>()
    private lateinit var adapter: ArrayAdapter<String>

    companion object {
        private const val TAG = "FavFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fav, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        lvLists = view.findViewById(R.id.lvFavLists)
        btnAddList = view.findViewById(R.id.buttonadd)

        // Initialize Adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        lvLists.adapter = adapter

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Veuillez vous connecter pour accéder aux favoris", Toast.LENGTH_LONG).show()

            dismiss()
            return view
        }

        // 🔹 Charger les listes existantes
        loadFavLists(currentUser.uid)

        // 🔹 Créer une nouvelle liste
        btnAddList.setOnClickListener {
            showCreateListDialog(currentUser.uid)
        }

        // 🔹 CLICK LISTENER: Show Products inside the list
        lvLists.setOnItemClickListener { _, _, position, _ ->
            val selectedList = favLists[position]
            showListDetailsDialog(selectedList)
        }

        // 🔹 Boutons Profil et Paramètre
        val btnProfil = view.findViewById<ImageButton>(R.id.buttonProfil)
        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)

        btnProfil.setOnClickListener {
            dismiss()
            ProfilFragment().show(parentFragmentManager, "ProfilFragment")
        }

        btnParametre.setOnClickListener {
            dismiss()
            ParametreFragment().show(parentFragmentManager, "ParametreFragment")
        }

        return view
    }

    // --- 1. Load Lists from Firestore ---
    private fun loadFavLists(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("favLists")
            .get()
            .addOnSuccessListener { snapshot ->
                favLists.clear()
                adapter.clear()
                for (doc in snapshot.documents) {
                    val name = doc.getString("name") ?: "Liste sans nom"

                    // Safely cast the products array
                    val products = doc.get("products") as? List<Map<String, Any>> ?: emptyList()

                    favLists.add(FavList(doc.id, name, products))
                    adapter.add(name)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error loading lists", e)
            }
    }

    // --- 2. Create New List Dialog ---
    private fun showCreateListDialog(userId: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Créer une nouvelle liste")
        val input = EditText(requireContext())
        input.hint = "Nom de la liste"
        builder.setView(input)

        builder.setPositiveButton("Créer") { dialog, _ ->
            val listName = input.text.toString().trim()
            if (listName.isNotEmpty()) {
                val newList = hashMapOf(
                    "name" to listName,
                    "products" to emptyList<Map<String, Any>>()
                )

                db.collection("users")
                    .document(userId)
                    .collection("favLists")
                    .add(newList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Liste créée", Toast.LENGTH_SHORT).show()
                        loadFavLists(userId)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error creating list", e)
                        Toast.makeText(requireContext(), "Erreur", Toast.LENGTH_SHORT).show()
                    }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // --- 3. SHOW PRODUCTS DIALOG & HANDLE CLICKS ---
    private fun showListDetailsDialog(favList: FavList) {
        val builder = AlertDialog.Builder(requireContext())

        // Custom Title View for the Dialog (Optional, keeps it clean)
        val titleView = TextView(requireContext())
        titleView.text = favList.name
        titleView.textSize = 22f
        titleView.setPadding(40, 40, 40, 20)
        titleView.setTextColor(resources.getColor(android.R.color.black, null))
        // Assuming you have the font, otherwise remove fontFamily line
        // titleView.typeface = resources.getFont(R.font.montserratbold)
        builder.setCustomTitle(titleView)

        if (favList.products.isEmpty()) {
            builder.setMessage("\n   Aucun produit dans cette liste.")
        } else {
            // 1. Create the Custom Adapter
            val customAdapter = FavProductAdapter(requireContext(), favList.products)

            // 2. Set the Adapter to the Dialog
            builder.setAdapter(customAdapter) { dialog, which ->

                // Get the data safely
                val selectedProductMap = favList.products[which]
                val name = selectedProductMap["name"] as? String ?: "Inconnu"
                val brand = selectedProductMap["brand"] as? String ?: "Marque inconnue"
                val store = selectedProductMap["store"] as? String
                val price = selectedProductMap["price"] as? String ?: "N/A"
                val image = selectedProductMap["image"] as? String ?: ""

                // 3. Open Product Fragment
                val fragment = ProductFragment.newInstance(name, brand, store, price, image)

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit()

                dialog.dismiss()
                dismiss()
            }
        }

        builder.setPositiveButton("Fermer") { dialog, _ -> dialog.dismiss() }

        // Show the dialog
        val dialog = builder.create()

        // Optional: Transparent background to let the CardViews rounded corners shine
        dialog.window?.setBackgroundDrawableResource(android.R.drawable.screen_background_light_transparent)

        dialog.show()

        // Optional: Styling the list dividers to be invisible (cleaner look)
        val listView = dialog.listView
        listView.divider = null
        listView.dividerHeight = 0
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    data class FavList(
        val id: String,
        val name: String,
        val products: List<Map<String, Any>>
    )
}



class FavProductAdapter(
    private val context: Context,
    private val dataSource: List<Map<String, Any>>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = dataSource.size

    override fun getItem(position: Int): Map<String, Any> = dataSource[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.item_fav_product, parent, false)

        // Get elements
        val tvName = rowView.findViewById<TextView>(R.id.tvItemName)
        val tvPrice = rowView.findViewById<TextView>(R.id.tvItemPrice)

        // Get data
        val item = getItem(position)
        val name = item["name"] as? String ?: "Inconnu"
        val price = item["price"] as? String ?: ""
        val brand = item["brand"] as? String ?: ""

        // Set text
        // Combining Brand + Name makes it look professional, but keeps 1 line limit
        tvName.text = if(brand.isNotEmpty()) "$brand - $name" else name
        tvPrice.text = if(price.isNotEmpty() && price != "N/A") "$price€" else ""

        return rowView
    }
}