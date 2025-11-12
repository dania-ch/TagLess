package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment

class FavFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_fav, container, false)

        // Récupération des boutons
        val btnProfil = view.findViewById<ImageButton>(R.id.buttonProfil)
        val btnParametre = view.findViewById<ImageButton>(R.id.buttonParametre)

        // 🟢 Ouvrir le fragment Profil
        btnProfil.setOnClickListener {
            dismiss() // fermer Favoris
            ProfilFragment().show(parentFragmentManager, "ProfilFragment")
        }

        // 🟢 Ouvrir le fragment Paramètres
        btnParametre.setOnClickListener {
            dismiss()
            ParametreFragment().show(parentFragmentManager, "ParametreFragment")
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
