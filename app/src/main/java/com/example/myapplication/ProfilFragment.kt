//package com.example.myapplication
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageButton
//import androidx.fragment.app.Fragment
//import android.app.AlertDialog
//
//class ProfilFragment :DialogFragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val view = inflater.inflate(R.layout.fragment_profil, container, false)
//
//        // Boutons Connexion et Inscription
//        val btnCnx = view.findViewById<Button>(R.id.buttoncnx)
//        val btnInscription = view.findViewById<Button>(R.id.buttonInscription)
//
//        btnCnx.setOnClickListener {
//            showConnexionDialog()
//        }
//
//        btnInscription.setOnClickListener {
//            showInscriptionDialog()
//        }
//
//        // ImageButtons
//        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)
//        val btnProfil = view.findViewById<ImageButton>(R.id.buttonProfil)
//        val btnFav = view.findViewById<ImageButton>(R.id.buttonFav)
//
//        btnParametre.setOnClickListener {
//            // Ouvrir le fragment Paramètre
//            ParametreFragment().show(parentFragmentManager, "ParametreFragment")
//        }
//
//        btnProfil.setOnClickListener {
//            // Ici, tu peux ajouter une action pour le profil (par exemple ouvrir un autre fragment)
//        }
//
//        btnFav.setOnClickListener {
//            // Ici, tu peux ajouter une action pour les favoris
//        }
//
//        return view
//    }
//
//    // Pop-up pour Connexion
//    private fun showConnexionDialog() {
//        val dialog = AlertDialog.Builder(requireContext())
//            .setTitle("Connexion")
//            .setMessage("Ici, vous pouvez mettre votre formulaire de connexion")
//            .setPositiveButton("OK", null)
//            .create()
//        dialog.show()
//    }
//
//    // Pop-up pour Inscription
//    private fun showInscriptionDialog() {
//        val dialog = AlertDialog.Builder(requireContext())
//            .setTitle("Créer un compte")
//            .setMessage("Ici, vous pouvez mettre votre formulaire d'inscription")
//            .setPositiveButton("OK", null)
//            .create()
//        dialog.show()
//    }
//}
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment

class ProfilFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        val btnCnx = view.findViewById<Button>(R.id.buttoncnx)
        val btnInscription = view.findViewById<Button>(R.id.buttonInscription)

        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)
        val btnFav = view.findViewById<ImageButton>(R.id.buttonFav)

        // 🔹 Se connecter → ouvrir ConnexionActivity
        btnCnx.setOnClickListener {
            dismiss()
            startActivity(Intent(requireContext(), ConnexionActivity::class.java))
        }

        // 🔹 Créer un compte → ouvrir InscriptionActivity
        btnInscription.setOnClickListener {
            dismiss()
            startActivity(Intent(requireContext(), InscriptionActivity::class.java))
        }

        // ⚙️ Paramètre
        btnParametre.setOnClickListener {
            dismiss()
            ParametreFragment().show(parentFragmentManager, "ParametreFragment")
        }

        // 💖 Favoris
        btnFav.setOnClickListener {
            // TODO: ouvrir les favoris
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

