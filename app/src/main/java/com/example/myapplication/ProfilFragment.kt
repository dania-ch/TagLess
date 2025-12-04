//
//
//package com.example.myapplication
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.DialogFragment
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//
//class ProfilFragment : DialogFragment() {
//
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreateView(
//
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val db = FirebaseFirestore.getInstance()
//
//
//        val view = inflater.inflate(R.layout.fragment_profil, container, false)
//        auth = FirebaseAuth.getInstance()
//        val currentUser = auth.currentUser
//
//        // TextView pour afficher email ou message bienvenue
//        val tvBienvenue = view.findViewById<TextView>(R.id.bienvenue)
//
//        // Boutons
//        val btnCnx = view.findViewById<Button>(R.id.buttoncnx)
//        val btnInscription = view.findViewById<Button>(R.id.buttonInscription)
//
//        val btnModifier = Button(requireContext()).apply { text = "Modifier infos" }
//        val btnDeconnexion = Button(requireContext()).apply { text = "Déconnexion" }
//        val btnSupprimer = Button(requireContext()).apply { text = "Supprimer compte" }
//
//        // ImageButtons
//        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)
//        val btnFav = view.findViewById<ImageButton>(R.id.buttonFav)
//
//        if (currentUser != null) {
//            // ✅ Utilisateur connecté
//            tvBienvenue.text = "Bienvenue, ${currentUser.email}"
//
//            // Masquer les boutons Connexion / Inscription
//            btnCnx.visibility = View.GONE
//            btnInscription.visibility = View.GONE
//
//            // Ajouter dynamiquement les boutons Modifier / Déconnexion / Supprimer
//            val layout = view as ViewGroup
//            layout.addView(btnModifier)
//            layout.addView(btnDeconnexion)
//            layout.addView(btnSupprimer)
//
//            // Gestion des clics
//            btnModifier.setOnClickListener {
//                val builder = android.app.AlertDialog.Builder(requireContext())
//                builder.setTitle("Modifier mes informations")
//
//                // Layout personnalisé pour le dialog
//                val viewDialog = layoutInflater.inflate(R.layout.dialog_modifier_infos, null)
//                val etNom = viewDialog.findViewById<EditText>(R.id.etNom)
//                val etEmail = viewDialog.findViewById<EditText>(R.id.etEmail)
//
//                // Pré-remplir avec les valeurs actuelles
//                etEmail.setText(currentUser.email)
//                db.collection("users").document(currentUser.uid).get().addOnSuccessListener { doc ->
//                    if (doc.exists()) {
//                        etNom.setText(doc.getString("nom"))
//                    }
//                }
//
//                builder.setView(viewDialog)
//
//                builder.setPositiveButton("Enregistrer") { dialog, _ ->
//                    val newEmail = etEmail.text.toString().trim()
//                    val newNom = etNom.text.toString().trim()
//
//                    if (newEmail.isEmpty() || newNom.isEmpty()) {
//                        Toast.makeText(requireContext(), "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
//                        return@setPositiveButton
//                    }
//
//                    // 🔹 Mettre à jour l'email Firebase Auth
//                    currentUser.updateEmail(newEmail).addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Toast.makeText(requireContext(), "Email mis à jour", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(requireContext(), "Erreur email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                        }
//                    }
//
//                    // 🔹 Mettre à jour le nom dans Firestore
//                    db.collection("users").document(currentUser.uid)
//                        .update("nom", newNom)
//                        .addOnSuccessListener {
//                            Toast.makeText(requireContext(), "Nom mis à jour", Toast.LENGTH_SHORT).show()
//                        }
//                        .addOnFailureListener { e ->
//                            Toast.makeText(requireContext(), "Erreur nom: ${e.message}", Toast.LENGTH_LONG).show()
//                        }
//
//                    dialog.dismiss()
//                }
//
//                builder.setNegativeButton("Annuler") { dialog, _ ->
//                    dialog.dismiss()
//                }
//
//                builder.show()
//            }
//
//
//            btnDeconnexion.setOnClickListener {
//                auth.signOut()
//                Toast.makeText(requireContext(), "Déconnecté", Toast.LENGTH_SHORT).show()
//                dismiss()
//            }
//
//            btnSupprimer.setOnClickListener {
//                currentUser.delete().addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(requireContext(), "Compte supprimé", Toast.LENGTH_SHORT).show()
//                        dismiss()
//                    } else {
//                        Toast.makeText(requireContext(), "Erreur suppression: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//
//        } else {
//            // 🔹 Utilisateur non connecté → boutons existants visibles
//            tvBienvenue.text = "Bienvenue ! Connectez-vous ou créez un compte"
//            btnCnx.setOnClickListener {
//                dismiss()
//                startActivity(Intent(requireContext(), ConnexionActivity::class.java))
//            }
//
//            btnInscription.setOnClickListener {
//                dismiss()
//                startActivity(Intent(requireContext(), InscriptionActivity::class.java))
//            }
//        }
//
//        // ⚙️ Paramètre
//        btnParametre.setOnClickListener {
//            dismiss()
//            ParametreFragment().show(parentFragmentManager, "ParametreFragment")
//        }
//
//        // 💖 Favoris
//        btnFav.setOnClickListener {
//            dismiss()
//            FavFragment().show(parentFragmentManager, "FavFragment")
//        }
//
//        return view
//    }
//
//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    }
//}


package com.example.myapplication

import android.content.Intent
import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        // 🔹 Référence aux boutons
        val btnCnx = view.findViewById<Button>(R.id.buttoncnx)
        val btnInscription = view.findViewById<Button>(R.id.buttonInscription)
        val btnModifier = view.findViewById<Button>(R.id.buttonModifier)
        val btnDeconnexion = view.findViewById<Button>(R.id.buttonDeconnexion)
        val btnSupprimer = view.findViewById<Button>(R.id.buttonSupprimer)
        val tvNom = view.findViewById<TextView>(R.id.tvNom)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)

        // 🔹 Si utilisateur connecté
        if (currentUser != null) {
            // Afficher les infos
            btnCnx.visibility = View.GONE
            btnInscription.visibility = View.GONE

            btnModifier.visibility = View.VISIBLE
            btnDeconnexion.visibility = View.VISIBLE
            btnSupprimer.visibility = View.VISIBLE
            tvNom.visibility = View.VISIBLE
            tvEmail.visibility = View.VISIBLE

            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        tvNom.text = doc.getString("nom")
                        tvEmail.text = currentUser.email
                    }
                }

            // 🔹 Modifier infos
            btnModifier.setOnClickListener {
                showModifierDialog(currentUser)
            }

            // 🔹 Déconnexion
            btnDeconnexion.setOnClickListener {
                auth.signOut()
                Toast.makeText(requireContext(), "Déconnecté", Toast.LENGTH_SHORT).show()
                dismiss() // Ferme le fragment
            }

            // 🔹 Supprimer compte
            btnSupprimer.setOnClickListener {
                val userId = currentUser.uid

                // Supprimer le document Firestore
                db.collection("users").document(userId).delete()
                    .addOnSuccessListener {
                        // Ensuite supprimer le compte Firebase Auth
                        currentUser.delete()
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Erreur suppression Auth: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Erreur suppression Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }


        } else {
            // 🔹 Si pas connecté → afficher bouton connexion / inscription
            btnCnx.visibility = View.VISIBLE
            btnInscription.visibility = View.VISIBLE

            btnModifier.visibility = View.GONE
            btnDeconnexion.visibility = View.GONE
            btnSupprimer.visibility = View.GONE
            tvNom.visibility = View.GONE
            tvEmail.visibility = View.GONE

            // Se connecter → ConnexionActivity
            btnCnx.setOnClickListener {
                dismiss()
                startActivity(Intent(requireContext(), ConnexionActivity::class.java))
            }

            // Créer un compte → InscriptionActivity
            btnInscription.setOnClickListener {
                dismiss()
                startActivity(Intent(requireContext(), InscriptionActivity::class.java))
            }
        }

        return view
    }

    private fun showModifierDialog(currentUser: com.google.firebase.auth.FirebaseUser) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Modifier mes informations")

        val viewDialog = layoutInflater.inflate(R.layout.dialog_modifier_infos, null)
        val etNom = viewDialog.findViewById<EditText>(R.id.etNom)
        val etEmail = viewDialog.findViewById<EditText>(R.id.etEmail)

        etEmail.setText(currentUser.email)
        db.collection("users").document(currentUser.uid).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                etNom.setText(doc.getString("name"))
            }
        }

        builder.setView(viewDialog)

        builder.setPositiveButton("Enregistrer") { dialog, _ ->
            val newEmail = etEmail.text.toString().trim()
            val newNom = etNom.text.toString().trim()

            if (newEmail.isEmpty() || newNom.isEmpty()) {
                Toast.makeText(requireContext(), "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Mettre à jour l'email Firebase Auth
            currentUser.updateEmail(newEmail).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Email mis à jour", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Erreur email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }

            // Mettre à jour le nom dans Firestore
            db.collection("users").document(currentUser.uid)
                .update("nom", newNom)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Nom mis à jour", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erreur nom: ${e.message}", Toast.LENGTH_LONG).show()
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
