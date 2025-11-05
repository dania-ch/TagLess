//package com.example.myapplication
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import androidx.fragment.app.DialogFragment
//
//class ParametreFragment : DialogFragment() {
//
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val view = inflater.inflate(R.layout.fragment_parametre, container, false)
//
////        val button = view.findViewById<Button>(R.id.button)
//
//
//
//
//
////        button.setOnClickListener {
////
////        }
//
//
//
//
//
//
//
//
//        return view
//    }
//
//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//            )
//        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent
//        )
//
//    }
//
//}

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

