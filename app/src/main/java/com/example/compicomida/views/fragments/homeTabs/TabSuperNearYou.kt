package com.example.compicomida.views.fragments.homeTabs

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class TabSuperNearYou : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    // Activity launcher to request user location permissions.
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions -> handleLocationPermissionResult(permissions) }

    /**
     * Initialize FusedLocationProviderClient --> To get the user's current location.
     * Initialize the Places API
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized())
            Places.initialize(requireContext(), CompiComidaApp.MAPS_API_KEY)
        placesClient = Places.createClient(requireContext())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    /**
     * Inflate the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab_super_near_you, container, false)
    }

    /**
     * Initialising Google Maps.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    /**
     * Once map is ready:
     * 1. Move to default location - Spain.
     * 2. Workflow of Permissions.
     * 2.1. Check if permissions are granted, if so, enable the my location layer.
     * 2.2 Rationale Dialog. Not implemented.
     * 2.3 Otherwise, request permission.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        defaultLocation() // Going to default location

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            goToLastLocation()
            return
        }

        // 2. Rationale Dialog

        // 3. Otherwise, request permission
        requestLocationPermission()
    }

    // Showing the Default Location - Spain
    private fun defaultLocation() {

        val so = LatLng(36.575879681180396, -10.174943261346256)
        val ne = LatLng(41.79667171849622, 5.641897541012162)
        val spain = LatLngBounds(so, ne)

        map.moveCamera(
            CameraUpdateFactory
                .newLatLngBounds(spain, 1080, 1080, 0)
        )
    }

    // 2.3 Otherwise, request location permissions.
    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 2.3 Handling location permission Result
    private fun handleLocationPermissionResult(permissions: Map<String, @JvmSuppressWildcards Boolean>) {
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d("SUPER-NEAR-YOU", "Fine Location granted.")
                handleLocationGranted()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("SUPER-NEAR-YOU", "Approx. Location granted.")
                handleLocationGranted()
            }

            else -> {
                Log.d("SUPER-NEAR-YOU", "Location permission denied.")
                handleDenyPermission()
            }
        }
    }

    // 2.3 Handling location granted.
    private fun handleLocationGranted() {

        // Get last known location and update the map
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) goToLastLocation()

    }

    @SuppressLint("MissingPermission") // CAREFUL! ONLY USE IT IF PERMISSIONS GRANTED
    private fun goToLastLocation() {

        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Move the camera to the user's location.
                    val userLocation = LatLng(location.latitude, location.longitude)
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(userLocation, 14f),
                        2000,
                        null
                    )

                    showNearestSupermarkets(location)
                }
            }
    }

    private fun showNearestSupermarkets(location: Location) {

        // Defining a list of fields to include in the response
        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.LOCATION
        )

        // Define circle radius in meters
        val center = LatLng(location.latitude, location.longitude)
        val circle = CircularBounds.newInstance(center, 1000.0) // 1km

        // Define list of types to include
        val type = listOf("supermarket")

        // Prepare NearbySearchRequest
        val nearbySearchRequest = SearchNearbyRequest
            .builder(circle, fields)
            .setIncludedTypes(type)
            .setMaxResultCount(3)
            .build()

        // Fetch results
        placesClient.searchNearby(nearbySearchRequest)
            .addOnSuccessListener { response ->
                response.places.forEach { place ->
                    val latLng =
                        LatLng(place.location?.latitude ?: 0.0, place.location?.longitude ?: 0.0)

                    // Add a marker for each supermarket
                    map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(place.displayName)
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.e("NearbySearch", "Error fetching nearby places: ${exception.message}")
            }


    }

    // 2.3 Handling location denied.
    private fun handleDenyPermission() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            // Used to tint the drawable
            val icon =
                getDrawable(requireContext(), R.drawable.priority_high_24px)?.mutate()

            icon?.let {
                DrawableCompat.setTint(
                    it,
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
                setIcon(it)
            }

            setTitle(R.string.tab_markets_near_denied_dialog_title)
            setMessage(R.string.tab_markets_near_denied_dialog_message)

            setPositiveButton(R.string.tab_markets_near_denied_dialog_positive_button) { dialog, _ ->
                dialog.dismiss()
            }

            show()
        }

    }

}