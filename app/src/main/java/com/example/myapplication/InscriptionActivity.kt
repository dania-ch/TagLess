package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val editNom = findViewById<EditText>(R.id.editNom)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnInscription = findViewById<Button>(R.id.btnValiderInscription)

        btnInscription.setOnClickListener {
            val nom = editNom.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            // Vérification des champs
            if (nom.isEmpty()) {
                editNom.error = "Veuillez entrer votre nom"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                editEmail.error = "Veuillez entrer un email"
                return@setOnClickListener
            }
            if (password.length < 6) {
                editPassword.error = "Min. 6 caractères"
                return@setOnClickListener
            }

            // Création compte Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    // Sauvegarde Firestore
                    val data = hashMapOf(
                        "name" to nom,
                        "email" to email
                    )

                    db.collection("users").document(uid)
                        .set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, ConnexionActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erreur Firestore", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_LONG).show()
                }
        }
    }
}
