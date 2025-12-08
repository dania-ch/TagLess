package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FavFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var lvLists: ListView
    private lateinit var btnAddList: ImageButton

    private val favLists = mutableListOf<FavList>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fav, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        lvLists = view.findViewById(R.id.lvFavLists)
        btnAddList = view.findViewById(R.id.buttonadd)

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

        // 🔹 Ajouter un produit dans une liste (exemple)
        lvLists.setOnItemClickListener { _, _, position, _ ->
            val selectedList = favLists[position]
            showAddProductDialog(currentUser.uid, selectedList)
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
                    val products = doc.get("products") as? List<Map<String, Any>> ?: emptyList()
                    favLists.add(FavList(doc.id, name, products))
                    adapter.add(name)
                }
                adapter.notifyDataSetChanged()
            }
    }

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
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    private fun showAddProductDialog(userId: String, favList: FavList) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ajouter un produit à '${favList.name}'")
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_add_product, null)
        builder.setView(view)

        val etName = view.findViewById<EditText>(R.id.etProductName)
        val etBrand = view.findViewById<EditText>(R.id.etProductBrand)
        val etStore = view.findViewById<EditText>(R.id.etProductStore)
        val etPrice = view.findViewById<EditText>(R.id.etProductPrice)
        val etImage = view.findViewById<EditText>(R.id.etProductImage)

        builder.setPositiveButton("Ajouter") { dialog, _ ->
            val product = hashMapOf(
                "name" to etName.text.toString().trim(),
                "brand" to etBrand.text.toString().trim(),
                "store" to etStore.text.toString().trim(),
                "price" to etPrice.text.toString().trim(),
                "image" to etImage.text.toString().trim()
            )
            db.collection("users")
                .document(userId)
                .collection("favLists")
                .document(favList.id)
                .update("products", FieldValue.arrayUnion(product))
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Produit ajouté", Toast.LENGTH_SHORT).show()
                }
            dialog.dismiss()
        }

        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }

        builder.show()
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
