package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Declare views at class level if needed, or find them inside functions
    private lateinit var tvNom: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        // Recupration des vues
        val btnProfil = view.findViewById<ImageButton>(R.id.buttonProfil)
        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)
        val btnFav = view.findViewById<ImageButton>(R.id.buttonFav)

        val btnCnx = view.findViewById<Button>(R.id.buttoncnx)
        val btnInscription = view.findViewById<Button>(R.id.buttonInscription)
        val btnModifier = view.findViewById<Button>(R.id.buttonModifier)
        val btnDeconnexion = view.findViewById<Button>(R.id.buttonDeconnexion)
        val btnSupprimer = view.findViewById<Button>(R.id.buttonSupprimer)
        tvNom = view.findViewById(R.id.tvNom) // Initialize here

        // Navigation Top Bar
        btnProfil.setOnClickListener {
            // Déjà sur le profil, rien à faire ou refresh
        }

        btnParametre.setOnClickListener {
            dismiss()
            ParametreFragment().show(parentFragmentManager, "ParametreFragment")
        }

        btnFav.setOnClickListener {
            dismiss()
            FavFragment().show(parentFragmentManager, "FavFragment")
        }

        // Logique Utilisateur cnt ou non
        if (currentUser != null) {
            // connecte
            btnCnx.visibility = View.GONE
            btnInscription.visibility = View.GONE

            btnModifier.visibility = View.VISIBLE
            btnDeconnexion.visibility = View.VISIBLE
            btnSupprimer.visibility = View.VISIBLE
            tvNom.visibility = View.VISIBLE

            // Charger le nom actuel depuis Firestore
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        tvNom.text = doc.getString("name")
                    }
                }

            // Modifier seulement le nom
            btnModifier.setOnClickListener {
                showModifierDialog(currentUser)
            }

            // Deconnexion
            btnDeconnexion.setOnClickListener {
                auth.signOut()
                Toast.makeText(requireContext(), "Déconnecté", Toast.LENGTH_SHORT).show()
                dismiss()
            }

            // Supprimer compte
            btnSupprimer.setOnClickListener {
                val userId = currentUser.uid
                db.collection("users").document(userId).delete()
                    .addOnSuccessListener {
                        currentUser.delete()
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Compte supprimé", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Erreur Auth: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Erreur Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }

        } else {
            // Non connecte
            btnCnx.visibility = View.VISIBLE
            btnInscription.visibility = View.VISIBLE

            btnModifier.visibility = View.GONE
            btnDeconnexion.visibility = View.GONE
            btnSupprimer.visibility = View.GONE
            tvNom.visibility = View.GONE

            btnCnx.setOnClickListener {
                dismiss()
                startActivity(Intent(requireContext(), ConnexionActivity::class.java))
            }

            btnInscription.setOnClickListener {
                dismiss()
                startActivity(Intent(requireContext(), InscriptionActivity::class.java))
            }
        }

        return view
    }

    private fun showModifierDialog(currentUser: com.google.firebase.auth.FirebaseUser) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Modifier mon nom")

        // MaJ
        val viewDialog = layoutInflater.inflate(R.layout.dialog_modifier_infos, null)
        val etNom = viewDialog.findViewById<EditText>(R.id.etNom)

//        existing
        val currentName = tvNom.text.toString()
        if (currentName != "Name") {
            etNom.setText(currentName)
        }

        builder.setView(viewDialog)

        builder.setPositiveButton("Enregistrer") { dialog, _ ->
            val newNom = etNom.text.toString().trim()

            if (newNom.isEmpty()) {
                Toast.makeText(requireContext(), "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Update Firestore seulement
            db.collection("users").document(currentUser.uid)
                .update("name", newNom)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Nom mis à jour", Toast.LENGTH_SHORT).show()
                    // Update the TextView immediately so the user sees the change
                    tvNom.text = newNom
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erreur mise à jour: ${e.message}", Toast.LENGTH_LONG).show()
                }

            dialog.dismiss()
        }

        builder.setNegativeButton("Annuler") { dialog, _ ->
            dialog.dismiss()
        }

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
}