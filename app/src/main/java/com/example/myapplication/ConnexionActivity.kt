package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityConnexionBinding
import com.google.firebase.auth.FirebaseAuth

class ConnexionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityConnexionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnexionBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_connexion)



        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnConnexion = findViewById<Button>(R.id.btnConnexion)
        val tvInscriptionLink = findViewById<TextView>(R.id.tvInscriptionLink)

        val btnProfil = findViewById<ImageButton>(R.id.buttonProfil)
        val btnParametre = findViewById<ImageButton>(R.id.buttonParametre)


// 🟢 Ouvrir le fragment Profil
        btnProfil.setOnClickListener {
            ProfilFragment().show(supportFragmentManager, "ProfilFragment")
        }

// 🟢 Ouvrir le fragment Paramètres
        btnParametre.setOnClickListener {
            ParametreFragment().show(supportFragmentManager, "ParametreFragment")
        }

// 🟢 Ouvrir le fragment Favoris
        val buttonFav = findViewById<ImageButton>(R.id.buttonFav)
        binding.buttonFav.setOnClickListener {
            val shared = getSharedPreferences("user_session", MODE_PRIVATE)
            val isLogged = shared.getBoolean("isLogged", false)

            if (!isLogged) {
                startActivity(Intent(this, ConnexionActivity::class.java))
            } else {
                FavFragment().show(supportFragmentManager, "FavFragment")
            }
        }



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
