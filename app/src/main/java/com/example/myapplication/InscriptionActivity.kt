//package com.example.myapplication
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.myapplication.databinding.ActivityChercherBinding
//import com.example.myapplication.databinding.ActivityInscriptionBinding
//import com.example.myapplication.databinding.ActivityMainBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class InscriptionActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore
//
//
//
//    private lateinit var binding: ActivityInscriptionBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//
//
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_inscription)
//        binding = ActivityInscriptionBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        binding.buttonFav.setOnClickListener {
//            val shared = getSharedPreferences("user_session", MODE_PRIVATE)
//            val isLogged = shared.getBoolean("isLogged", false)
//
//            if (!isLogged) {
//                startActivity(Intent(this, ConnexionActivity::class.java))
//            } else {
//                FavFragment().show(supportFragmentManager, "FavFragment")
//            }
//        }
//
//        val btnProfil = findViewById<ImageButton>(R.id.buttonProfil)
//        val btnParametre = findViewById<ImageButton>(R.id.buttonParametre)
////        val btnFav = findViewById<ImageButton>(R.id.buttonFav)
//
//// 🟢 Ouvrir le fragment Profil
//        btnProfil.setOnClickListener {
//            ProfilFragment().show(supportFragmentManager, "ProfilFragment")
//        }
//
//// 🟢 Ouvrir le fragment Paramètres
//        btnParametre.setOnClickListener {
//            ParametreFragment().show(supportFragmentManager, "ParametreFragment")
//        }
//
//
//
//        class ChercherActivity : AppCompatActivity() {
//
//            private lateinit var binding: ActivityChercherBinding
//
//            override fun onCreate(savedInstanceState: Bundle?) {
//                super.onCreate(savedInstanceState)
//
//                binding = ActivityChercherBinding.inflate(layoutInflater)
//                setContentView(binding.root)
//
//                // Bouton Favoris
//                binding.btnFav.setOnClickListener {
//                    val shared = getSharedPreferences("user_session", MODE_PRIVATE)
//                    val isLogged = shared.getBoolean("isLogged", false)
//
//                    if (!isLogged) {
//                        startActivity(Intent(this, ConnexionActivity::class.java))
//                    } else {
//                        FavFragment().show(supportFragmentManager, "FavFragment")
//                    }
//                }
//            }
//        }
//
//
//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance()
//
//        val editNom = findViewById<EditText>(R.id.editNom)
//        val editEmail = findViewById<EditText>(R.id.editEmail)
//        val editPassword = findViewById<EditText>(R.id.editPassword)
//        val btnInscription = findViewById<Button>(R.id.btnValiderInscription)
//
//        btnInscription.setOnClickListener {
//            val nom = editNom.text.toString().trim()
//            val email = editEmail.text.toString().trim()
//            val password = editPassword.text.toString().trim()
//
//            // Vérification des champs
//            if (nom.isEmpty()) {
//                editNom.error = "Veuillez entrer votre nom"
//                return@setOnClickListener
//            }
//            if (email.isEmpty()) {
//                editEmail.error = "Veuillez entrer un email"
//                return@setOnClickListener
//            }
//            if (password.length < 6) {
//                editPassword.error = "Min. 6 caractères"
//                return@setOnClickListener
//            }
//
//            // Création compte Firebase Auth
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnSuccessListener { result ->
//                    val uid = result.user?.uid ?: return@addOnSuccessListener
//
//                    // Sauvegarde Firestore
//                    val data = hashMapOf(
//                        "name" to nom,
//                        "email" to email
//                    )
//
//                    db.collection("users").document(uid)
//                        .set(data)
//                        .addOnSuccessListener {
//                            Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show()
//                            startActivity(Intent(this, ConnexionActivity::class.java))
//                            finish()
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(this, "Erreur Firestore", Toast.LENGTH_LONG).show()
//                        }
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_LONG).show()
//                }
//        }
//    }
//}


package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityInscriptionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInscriptionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 🔹 Bouton Favoris
        binding.buttonFav.setOnClickListener {
            val shared = getSharedPreferences("user_session", MODE_PRIVATE)
            val isLogged = shared.getBoolean("isLogged", false)

            if (!isLogged) {
                startActivity(Intent(this, ConnexionActivity::class.java))
            } else {
                FavFragment().show(supportFragmentManager, "FavFragment")
            }
        }

        // 🔹 Boutons Profil et Paramètre
        binding.buttonProfil.setOnClickListener {
            ProfilFragment().show(supportFragmentManager, "ProfilFragment")
        }
        binding.buttonParametre.setOnClickListener {
            ParametreFragment().show(supportFragmentManager, "ParametreFragment")
        }

        // 🔹 Inscription
        binding.btnValiderInscription.setOnClickListener {
            val nom = binding.editNom.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()

            // Vérification des champs
            if (nom.isEmpty()) {
                binding.editNom.error = "Veuillez entrer votre nom"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.editEmail.error = "Veuillez entrer un email"
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.editPassword.error = "Min. 6 caractères"
                return@setOnClickListener
            }

            // Création compte Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    // Sauvegarde Firestore
                    val userData = hashMapOf(
                        "name" to nom,
                        "email" to email
                    )

                    db.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            // 🔹 Créer une sous-collection "listes" vide
                            val defaultList = hashMapOf(
                                "name" to "Ma première liste",
                                "products" to listOf<Map<String, Any>>() // vide au départ
                            )
                            db.collection("users")
                                .document(uid)
                                .collection("listes")
                                .add(defaultList)

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
