//package com.example.myapplication
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageButton
//import androidx.fragment.app.DialogFragment
//
//class ProfilFragment : DialogFragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val view = inflater.inflate(R.layout.fragment_profil, container, false)
//
//        val btnCnx = view.findViewById<Button>(R.id.buttoncnx)
//        val btnInscription = view.findViewById<Button>(R.id.buttonInscription)
//
//        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)
//        val btnFav = view.findViewById<ImageButton>(R.id.buttonFav)
//
//        // 🔹 Se connecter → ouvrir ConnexionActivity
//        btnCnx.setOnClickListener {
//            dismiss()
//            startActivity(Intent(requireContext(), ConnexionActivity::class.java))
//        }
//
//        // 🔹 Créer un compte → ouvrir InscriptionActivity
//        btnInscription.setOnClickListener {
//            dismiss()
//            startActivity(Intent(requireContext(), InscriptionActivity::class.java))
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
//


package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfilFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val db = FirebaseFirestore.getInstance()


        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // TextView pour afficher email ou message bienvenue
        val tvBienvenue = view.findViewById<TextView>(R.id.bienvenue)

        // Boutons
        val btnCnx = view.findViewById<Button>(R.id.buttoncnx)
        val btnInscription = view.findViewById<Button>(R.id.buttonInscription)

        val btnModifier = Button(requireContext()).apply { text = "Modifier infos" }
        val btnDeconnexion = Button(requireContext()).apply { text = "Déconnexion" }
        val btnSupprimer = Button(requireContext()).apply { text = "Supprimer compte" }

        // ImageButtons
        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)
        val btnFav = view.findViewById<ImageButton>(R.id.buttonFav)

        if (currentUser != null) {
            // ✅ Utilisateur connecté
            tvBienvenue.text = "Bienvenue, ${currentUser.email}"

            // Masquer les boutons Connexion / Inscription
            btnCnx.visibility = View.GONE
            btnInscription.visibility = View.GONE

            // Ajouter dynamiquement les boutons Modifier / Déconnexion / Supprimer
            val layout = view as ViewGroup
            layout.addView(btnModifier)
            layout.addView(btnDeconnexion)
            layout.addView(btnSupprimer)

            // Gestion des clics
            btnModifier.setOnClickListener {
                val builder = android.app.AlertDialog.Builder(requireContext())
                builder.setTitle("Modifier mes informations")

                // Layout personnalisé pour le dialog
                val viewDialog = layoutInflater.inflate(R.layout.dialog_modifier_infos, null)
                val etNom = viewDialog.findViewById<EditText>(R.id.etNom)
                val etEmail = viewDialog.findViewById<EditText>(R.id.etEmail)

                // Pré-remplir avec les valeurs actuelles
                etEmail.setText(currentUser.email)
                db.collection("users").document(currentUser.uid).get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        etNom.setText(doc.getString("nom"))
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

                    // 🔹 Mettre à jour l'email Firebase Auth
                    currentUser.updateEmail(newEmail).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Email mis à jour", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Erreur email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                    // 🔹 Mettre à jour le nom dans Firestore
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


            btnDeconnexion.setOnClickListener {
                auth.signOut()
                Toast.makeText(requireContext(), "Déconnecté", Toast.LENGTH_SHORT).show()
                dismiss()
            }

            btnSupprimer.setOnClickListener {
                currentUser.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Compte supprimé", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Erreur suppression: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            // 🔹 Utilisateur non connecté → boutons existants visibles
            tvBienvenue.text = "Bienvenue ! Connectez-vous ou créez un compte"
            btnCnx.setOnClickListener {
                dismiss()
                startActivity(Intent(requireContext(), ConnexionActivity::class.java))
            }

            btnInscription.setOnClickListener {
                dismiss()
                startActivity(Intent(requireContext(), InscriptionActivity::class.java))
            }
        }

        // ⚙️ Paramètre
        btnParametre.setOnClickListener {
            dismiss()
            ParametreFragment().show(parentFragmentManager, "ParametreFragment")
        }

        // 💖 Favoris
        btnFav.setOnClickListener {
            dismiss()
            FavFragment().show(parentFragmentManager, "FavFragment")
        }

        return view
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
