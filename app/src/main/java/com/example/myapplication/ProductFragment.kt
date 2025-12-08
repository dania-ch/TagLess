////package com.example.myapplication
////
////import android.content.Intent
////import android.net.Uri
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import androidx.fragment.app.Fragment
////import com.bumptech.glide.Glide
////import com.example.myapplication.databinding.FragmentProductBinding
////
////class ProductFragment : Fragment() {
////
////
////    private var _binding: FragmentProductBinding? = null
////    private val binding get() = _binding!!
////
////    private var name: String = "Nom inconnu"
////    private var brand: String = "Marque inconnue"
////    private var store: String = "Magasin inconnu"
////    private var price: String = "N/A"
////    private var image: String? = null
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////
////        arguments?.let {
////            name = it.getString("name", name)
////            brand = it.getString("brand", brand)
////            store = it.getString("store", store)
////            price = it.getString("price", price)
////            image = it.getString("image")
////        }
////
////
////
////
////
////
////    }
////
////    override fun onCreateView(
////        inflater: LayoutInflater,
////        container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        _binding = FragmentProductBinding.inflate(inflater, container, false)
////        return binding.root
////    }
////
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////        //  Remplir les champs
////        binding.tvName.text = name
////        binding.tvBrand.text = brand
////        binding.tvStore.text = store
////        binding.tvPrice.text = "$price "
////
////        // Image du produit
////        if (!image.isNullOrEmpty()) {
////            Glide.with(requireContext())
////                .load(image)
////                .centerInside() // pour afficher l'image entière
////                .into(binding.ivProduct)
////        }
////
////        //  Bouton Scanner un autre produit
////        binding.btnRescan.setOnClickListener {
////            activity?.finish() // fermer le fragment/activity actuel
////        }
////
//////        binding.btnOpenMaps.setOnClickListener {
//////            val uri = Uri.parse("geo:0,0?q=${Uri.encode(store)}")
//////            val intent = Intent(Intent.ACTION_VIEW, uri)
//////            intent.setPackage("com.google.android.apps.maps")
//////            startActivity(intent)
//////        }
////
////        binding.btnOpenMaps.setOnClickListener {
////            val uri = Uri.parse("geo:0,0?q=${Uri.encode(store)}")
////            val intent = Intent(Intent.ACTION_VIEW, uri)
////            intent.setPackage("com.google.android.apps.maps")
////            startActivity(intent)
////        }
////
////
////
//////
////
////
////
////
////
////
////    }
////
////    override fun onDestroyView() {
////        super.onDestroyView()
////        _binding = null
////    }
////
////    companion object {
////        fun newInstance(
////            name: String,
////            brand: String,
////            store: String,
////            price: String,
////            image: String
////        ) = ProductFragment().apply {
////            arguments = Bundle().apply {
////                putString("name", name)
////                putString("brand", brand)
////                putString("store", store)
////                putString("price", price)
////                putString("image", image)
////            }
////        }
////    }
////}
//
//
//package com.example.myapplication
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import com.bumptech.glide.Glide
//import com.example.myapplication.databinding.FragmentProductBinding
//
//class ProductFragment : Fragment() {
//
//    private var _binding: FragmentProductBinding? = null
//    private val binding get() = _binding!!
//
//    private var name: String = "Nom inconnu"
//    private var brand: String = "Marque inconnue"
//    private var store: String? = null
//    private var price: String = "N/A"
//    private var image: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        arguments?.let {
//            name = it.getString("name", name)
//            brand = it.getString("brand", brand)
//            store = it.getString("store")         // peut être null
//            price = it.getString("price", price)
//            image = it.getString("image")
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentProductBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Affichage
//        binding.tvName.text = name
//        binding.tvBrand.text = brand
//
//        // Si aucun magasin trouvé → afficher message clair
//        if (store.isNullOrEmpty() || store == "Magasin inconnu") {
//            binding.tvStore.text = "Magasin non fourni"
//            binding.btnOpenMaps.isEnabled = false
//            binding.btnOpenMaps.alpha = 0.4f
//        } else {
//            binding.tvStore.text = store
//            binding.btnOpenMaps.isEnabled = true
//            binding.btnOpenMaps.alpha = 1f
//        }
//
//        binding.tvPrice.text = price
//
//        // Image du produit
//        if (!image.isNullOrEmpty()) {
//            Glide.with(requireContext())
//                .load(image)
//                .centerInside()
//                .into(binding.ivProduct)
//        }
//
//        // Rescan bouton
//        binding.btnRescan.setOnClickListener {
//            activity?.finish()
//        }
//
//        // Localisation uniquement si magasin valide
//        binding.btnOpenMaps.setOnClickListener {
//            if (!store.isNullOrEmpty()) {
//                val uri = Uri.parse("geo:0,0?q=${Uri.encode(store!!)}")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                intent.setPackage("com.google.android.apps.maps")
//                startActivity(intent)
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        fun newInstance(
//            name: String,
//            brand: String,
//            store: String?,
//            price: String,
//            image: String
//        ) = ProductFragment().apply {
//            arguments = Bundle().apply {
//                putString("name", name)
//                putString("brand", brand)
//                putString("store", store)
//                putString("price", price)
//                putString("image", image)
//            }
//        }
//    }
//}


package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentProductBinding

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private var name: String = "Nom inconnu"
    private var brand: String = "Marque inconnue"
    private var store: String? = null
    private var price: String = "N/A"
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("name", name)
            brand = it.getString("brand", brand)
            store = it.getString("store")         // peut être null
            price = it.getString("price", price)
            image = it.getString("image")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Affichage
        binding.tvName.text = name
        binding.tvBrand.text = brand

        // Si aucun magasin trouvé → afficher message clair
        if (store.isNullOrEmpty() || store == "Magasin inconnu") {
            binding.tvStore.text = "Magasin non fourni"
            binding.btnOpenMaps.isEnabled = false
            binding.btnOpenMaps.alpha = 0.4f
        } else {
            binding.tvStore.text = store
            binding.btnOpenMaps.isEnabled = true
            binding.btnOpenMaps.alpha = 1f
        }

        binding.tvPrice.text = "${price}€"

        // Image du produit
        if (!image.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(image)
                .centerInside()
                .into(binding.ivProduct)
        }

        // Rescan bouton
        binding.btnRescan.setOnClickListener {
            activity?.finish()
        }

        // Localisation uniquement si magasin valide
        binding.btnOpenMaps.setOnClickListener {
            if (!store.isNullOrEmpty()) {
                val uri = Uri.parse("geo:0,0?q=${Uri.encode(store!!)}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            name: String,
            brand: String,
            store: String?,
            price: String,
            image: String
        ) = ProductFragment().apply {
            arguments = Bundle().apply {
                putString("name", name)
                putString("brand", brand)
                putString("store", store)
                putString("price", price)
                putString("image", image)
            }
        }
    }
}
