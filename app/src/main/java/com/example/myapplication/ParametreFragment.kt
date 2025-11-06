package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.*
import android.widget.Button
import android.app.AlertDialog
import android.widget.ImageButton


class ParametreFragment : DialogFragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parametre, container, false)

        // SharedPreferences pour enregistrer la langue
        sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        val spinnerLangue = view.findViewById<Spinner>(R.id.spinnerLangue)
        val langues = resources.getStringArray(R.array.langues)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, langues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLangue.adapter = adapter

        // Charger la langue déjà enregistrée
        val savedLang = sharedPreferences.getString("app_lang", "fr")
        spinnerLangue.setSelection(if (savedLang == "en") 1 else 0)

        spinnerLangue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val languageCode = if (position == 0) "fr" else "en"
                if (languageCode != savedLang) {
                    saveLanguage(languageCode)
                    setLocale(languageCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val btnContact = view.findViewById<Button>(R.id.btnContact)
        val btnAbout = view.findViewById<Button>(R.id.btnAbout)

// Contact Support popup
        btnContact.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Contact Support")
                .setMessage("Vous pouvez nous contacter à : support@monsite.com\nNous répondrons rapidement !")
                .setPositiveButton("OK", null)
                .create()
            dialog.show()
        }

// À propos popup
        btnAbout.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("@string/apropos")
                .setMessage("@string/mssgapropos")
                .setPositiveButton("OK", null)
                .create()
            dialog.show()
        }



        val btnProfil = view.findViewById<ImageButton>(R.id.buttonProfil)

        btnProfil.setOnClickListener {
            // Ferme le fragment Paramètre (facultatif si tu veux qu’il disparaisse)
            dismiss()

            // Ouvre le fragment Profil comme une boîte de dialogue
            val profilFragment = ProfilFragment()
            profilFragment.show(parentFragmentManager, "ProfilFragment")
        }




        return view
    }

    private fun saveLanguage(languageCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString("app_lang", languageCode)
        editor.apply()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = requireContext().resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        dismissAllowingStateLoss()
        saveLanguage(languageCode)



        // Redémarrer proprement l'activité
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)

        Toast.makeText(requireContext(),
            if (languageCode == "fr") "Langue changée en Français 🇫🇷"
            else "Language changed to English 🇬🇧",
            Toast.LENGTH_SHORT
        ).show()
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

