package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private var mapView: MapView? = null

    // Coordonnées du magasin (remplace par celles de la way 27108404)
    private val shopLat = 48.8566
    private val shopLng = 2.3522

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)

        // Configurer OSMDroid
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("prefs", 0))
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setBuiltInZoomControls(true)
        mapView?.setMultiTouchControls(true)

        // Centrer la carte sur le magasin
        val startPoint = GeoPoint(shopLat, shopLng)
        mapView?.controller?.setZoom(15.0)
        mapView?.controller?.setCenter(startPoint)

        // Ajouter un marker
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.title = "Magasin"
        mapView?.overlays?.add(marker)

        return view
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
}
