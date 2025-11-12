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

