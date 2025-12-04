package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth

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

class ConnexionActivityN : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnConnexion = findViewById<Button>(R.id.btnConnexion)
        val tvInscriptionLink = findViewById<android.widget.TextView>(R.id.tvInscriptionLink)

        // Lien inscription
        tvInscriptionLink.setOnClickListener {
            startActivity(
                _root_ide_package_.android.content.Intent(
                    this,
                    InscriptionActivity::class.java
                )
            )
        }

        btnConnexion.setOnClickListener {
            val emailInput = email.text.toString().trim()
            val passwordInput = password.text.toString().trim()

            // Vérification des champs
            if (emailInput.isEmpty()) {
                email.error = "L'email est obligatoire"
                return@setOnClickListener
            }
            if (passwordInput.isEmpty()) {
                password.error = "Le mot de passe est obligatoire"
                return@setOnClickListener
            }

            // Connexion Firebase
            auth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnSuccessListener {
                    android.widget.Toast.makeText(this, "Connexion réussie", android.widget.Toast.LENGTH_SHORT).show()
                    startActivity(
                        _root_ide_package_.android.content.Intent(
                            this,
                            MainActivity::class.java
                        )
                    )
                    finish() // Empêche le retour vers connexion
                }
                .addOnFailureListener {
                    android.widget.Toast.makeText(this, "Email ou mot de passe incorrect", android.widget.Toast.LENGTH_LONG).show()
                }
        }
    }
}