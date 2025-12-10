package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {

    private var mapView: MapView? = null
    private var locationOverlay: MyLocationNewOverlay? = null

    private val shopLat = 48.8566
    private val shopLng = 2.3522
    private val LOCATION_PERMISSION_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        val btnFindShop = view.findViewById<Button>(R.id.btnFindShop)

        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("prefs", 0))
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setBuiltInZoomControls(true)
        mapView?.setMultiTouchControls(true)

        // Centrer initialement sur le magasin
        val shopPoint = GeoPoint(shopLat, shopLng)
        mapView?.controller?.setZoom(15.0)
        mapView?.controller?.setCenter(shopPoint)


        val marker = Marker(mapView)
        marker.position = shopPoint
        marker.title = "Magasin"
        mapView?.overlays?.add(marker)

        // Ajouter overlay de localisation si permissions ok
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            addUserLocationOverlay()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }

        // Bouton pour recentrer sur le magasin depuis position utilisateur
        btnFindShop.setOnClickListener {
            locationOverlay?.myLocation?.let { userLoc ->
                val userPoint = GeoPoint(userLoc.latitude, userLoc.longitude)
                mapView?.controller?.animateTo(userPoint)
                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLoc.latitude, userLoc.longitude,
                    shopLat, shopLng,
                    distance
                )
                println("Distance jusqu'au magasin : ${distance[0]} mètres")
            }
        }

        return view
    }

    private fun addUserLocationOverlay() {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        locationOverlay?.enableMyLocation()
        mapView?.overlays?.add(locationOverlay)
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDetach()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addUserLocationOverlay()
        }
    }
}
