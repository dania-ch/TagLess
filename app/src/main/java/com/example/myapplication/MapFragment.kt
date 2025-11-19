
package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

class MapFragment : Fragment() {

    private var mapView: MapView? = null

    // Coordonnées du magasin
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

        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("prefs", 0))
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setBuiltInZoomControls(true)
        mapView?.setMultiTouchControls(true)

        // Centrer la carte sur le magasin
        val shopPoint = GeoPoint(shopLat, shopLng)
        mapView?.controller?.setZoom(15.0)
        mapView?.controller?.setCenter(shopPoint)

        // Ajouter un marker pour le magasin
        val marker = Marker(mapView)
        marker.position = shopPoint
        marker.title = "Magasin"
        mapView?.overlays?.add(marker)

        // Ajouter overlay de localisation de l'utilisateur
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            addUserLocationOverlay()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }

        return view
    }

    private fun addUserLocationOverlay() {
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        locationOverlay.enableMyLocation()
        mapView?.overlays?.add(locationOverlay)

        // Calculer la distance entre l'utilisateur et le magasin
        locationOverlay.runOnFirstFix {
            val userLocation = locationOverlay.myLocation
            if (userLocation != null) {
                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude,
                    userLocation.longitude,
                    shopLat,
                    shopLng,
                    distance
                )
                println("Distance jusqu'au magasin : ${distance[0]} mètres")
            }
        }
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
