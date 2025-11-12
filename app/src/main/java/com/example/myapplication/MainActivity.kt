package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val languageCode = getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("app_lang", "fr")
        setLocale(this, languageCode!!)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonScanner.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }

//
        binding.buttonParametre.setOnClickListener {
            ParametreFragment().show(supportFragmentManager, "ParametreFragment")
        }

        binding.buttonProfil.setOnClickListener {
            ProfilFragment().show(supportFragmentManager, "ProfilFragment")
        }

        binding.buttonFav.setOnClickListener {
            FavFragment().show(supportFragmentManager, "FavFragment")
        }
    }

    fun setLocale(context: Context, languageCode: String){
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }



}

