//package com.example.myapplication
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import com.bumptech.glide.Glide
//import com.example.myapplication.databinding.FragmentProductBinding
//
//class ProductFragment : Fragment() {
//
//    private var _binding: FragmentProductBinding? = null
//    private val binding get() = _binding!!
//
//    private var name: String = "Nom inconnu"
//    private var brand: String = "Marque inconnue"
//    private var store: String? = null
//    private var price: String = "N/A"
//    private var image: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        arguments?.let {
//            name = it.getString("name", name)
//            brand = it.getString("brand", brand)
//            store = it.getString("store")         // peut être null
//            price = it.getString("price", price)
//            image = it.getString("image")
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentProductBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Affichage
//        binding.tvName.text = name
//        binding.tvBrand.text = brand
//
//        // Si aucun magasin trouvé → afficher message clair
//        if (store.isNullOrEmpty() || store == "Magasin inconnu") {
//            binding.tvStore.text = "Magasin non fourni"
//            binding.btnOpenMaps.isEnabled = false
//            binding.btnOpenMaps.alpha = 0.4f
//        } else {
//            binding.tvStore.text = store
//            binding.btnOpenMaps.isEnabled = true
//            binding.btnOpenMaps.alpha = 1f
//        }
//
//        binding.tvPrice.text = "${price}€"
//
//        // Image du produit
//        if (!image.isNullOrEmpty()) {
//            Glide.with(requireContext())
//                .load(image)
//                .centerInside()
//                .into(binding.ivProduct)
//        }
//
//        // Rescan bouton
//        binding.btnRescan.setOnClickListener {
//            activity?.finish()
//        }
//
//        // Localisation uniquement si magasin valide
//        binding.btnOpenMaps.setOnClickListener {
//            if (!store.isNullOrEmpty()) {
//                val uri = Uri.parse("geo:0,0?q=${Uri.encode(store!!)}")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                intent.setPackage("com.google.android.apps.maps")
//                startActivity(intent)
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        fun newInstance(
//            name: String,
//            brand: String,
//            store: String?,
//            price: String,
//            image: String
//        ) = ProductFragment().apply {
//            arguments = Bundle().apply {
//                putString("name", name)
//                putString("brand", brand)
//                putString("store", store)
//                putString("price", price)
//                putString("image", image)
//            }
//        }
//    }
//}


package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    // Firebase instances
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Data Holders
    private var name: String = "Nom inconnu"
    private var brand: String = "Marque inconnue"
    private var store: String? = null
    private var price: String = "N/A"
    private var image: String? = null

    // For the dialog lists
    private val listNames = mutableListOf<String>()
    private val listIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        arguments?.let {
            name = it.getString("name", name)
            brand = it.getString("brand", brand)
            store = it.getString("store")         // peut être null
            price = it.getString("price", price)
            image = it.getString("image")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- UI Initialization ---
        binding.tvName.text = name
        binding.tvBrand.text = brand

        // Si aucun magasin trouvé → afficher message clair
        if (store.isNullOrEmpty() || store == "Magasin inconnu") {
            binding.tvStore.text = "Magasin non fourni"
            binding.btnOpenMaps.isEnabled = false
            binding.btnOpenMaps.alpha = 0.4f
        } else {
            binding.tvStore.text = store
            binding.btnOpenMaps.isEnabled = true
            binding.btnOpenMaps.alpha = 1f
        }

        binding.tvPrice.text = "${price}€"

        if (!image.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(image)
                .centerInside()
                .into(binding.ivProduct)
        }

        // --- Buttons Listeners ---

        binding.btnRescan.setOnClickListener {
            activity?.finish()
        }

        binding.btnOpenMaps.setOnClickListener {
            if (!store.isNullOrEmpty()) {
                val uri = Uri.parse("geo:0,0?q=${Uri.encode(store!!)}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
        }

        // 🔹 FAB LISTENER: Add To Favorites
        binding.fabAddFav.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                // Fetch lists and then show dialog
                loadListsAndShowDialog(user.uid)
            } else {
                Toast.makeText(requireContext(), "Connectez-vous pour ajouter aux favoris", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- LOGIC: Fetch Lists ---
    private fun loadListsAndShowDialog(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("favLists")
            .get()
            .addOnSuccessListener { documents ->
                listNames.clear()
                listIds.clear()

                for (document in documents) {
                    val listName = document.getString("name") ?: "Liste sans nom"
                    listNames.add(listName)
                    listIds.add(document.id)
                }

                // Append the "Create new" option at the end
                listNames.add("+ Créer une nouvelle liste")

                showSelectionDialog(userId)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erreur de chargement des listes", Toast.LENGTH_SHORT).show()
            }
    }

    // --- LOGIC: Show Selection Dialog ---
    private fun showSelectionDialog(userId: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choisir une liste")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listNames)

        builder.setAdapter(adapter) { dialog, which ->
            // Check if the user clicked the LAST item (which is "+ Créer une nouvelle liste")
            if (which == listNames.size - 1) {
                // User wants to create a new list
                showCreateNewListDialog(userId)
            } else {
                // User selected an existing list
                val selectedListId = listIds[which]
                val selectedListName = listNames[which]
                addProductToFirestoreList(userId, selectedListId, selectedListName)
            }
        }

        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // --- LOGIC: Create New List Dialog ---
    private fun showCreateNewListDialog(userId: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Nouvelle liste")

        val input = EditText(requireContext())
        input.hint = "Nom de la liste"
        builder.setView(input)

        builder.setPositiveButton("Créer") { dialog, _ ->
            val newListName = input.text.toString().trim()
            if (newListName.isNotEmpty()) {

                // 1. Create the new list structure
                val newListMap = hashMapOf(
                    "name" to newListName,
                    "products" to emptyList<Any>() // Initialize empty
                )

                // 2. Add to Firestore
                db.collection("users")
                    .document(userId)
                    .collection("favLists")
                    .add(newListMap)
                    .addOnSuccessListener { docRef ->
                        // 3. Immediately add the product to this new list
                        addProductToFirestoreList(userId, docRef.id, newListName)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erreur création liste", Toast.LENGTH_SHORT).show()
                    }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // --- LOGIC: Save Product to Firestore ---
    private fun addProductToFirestoreList(userId: String, listId: String, listName: String) {
        // Create the product object map
        val productData = hashMapOf(
            "name" to name,
            "brand" to brand,
            "store" to store,
            "price" to price,
            "image" to image
        )

        // Use arrayUnion to append to the 'products' array field
        db.collection("users")
            .document(userId)
            .collection("favLists")
            .document(listId)
            .update("products", FieldValue.arrayUnion(productData))
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Ajouté à '$listName'", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            name: String,
            brand: String,
            store: String?,
            price: String,
            image: String
        ) = ProductFragment().apply {
            arguments = Bundle().apply {
                putString("name", name)
                putString("brand", brand)
                putString("store", store)
                putString("price", price)
                putString("image", image)
            }
        }
    }
}