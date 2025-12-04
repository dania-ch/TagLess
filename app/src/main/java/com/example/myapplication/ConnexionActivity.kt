package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ConnexionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnConnexion = findViewById<Button>(R.id.btnConnexion)
        val tvInscriptionLink = findViewById<TextView>(R.id.tvInscriptionLink)

        // Lien inscription
        tvInscriptionLink.setOnClickListener {
            startActivity(Intent(this, InscriptionActivity::class.java))
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
                    Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Empêche le retour vers connexion
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_LONG).show()
                }
        }
    }
}
