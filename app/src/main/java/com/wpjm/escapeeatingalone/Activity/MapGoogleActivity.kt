package com.wpjm.escapeeatingalone.Activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.wpjm.escapeeatingalone.R
import java.util.*

class MapGoogleActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_google)
        Places.initialize(applicationContext, "AIzaSyAOiIgD5-titJQRG5jgT_oKgmQl_PfuaVw", Locale.KOREA
        )
        
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        val placesClient = Places.createClient(this)
        val zoom: CameraUpdate = CameraUpdateFactory.zoomTo(16f) // 범위 높을수록 확대가 커집니다.

        mapFragment.getMapAsync(this)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME)) // Specify the types of place data to return.

        // place search 핸들러
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.e(
                    "googleMAP",
                    "Place: ${place.name}, ${place.id}, ${place.types}. ${place.address}, ${place.latLng}"
                )
                mMap.addMarker(MarkerOptions().position(LatLng(35.1477483, 129.128991)).title(place.name!!))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(35.1477483, 129.128991)))
                mMap.animateCamera(zoom) // 줌을 당긴다

//                mMap.addMarker(MarkerOptions().position(place.latLng!!).title(place.name!!))
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.latLng!!))
            }

            override fun onError(status: Status) {
                Log.e("googleMAP", "An error occurred: $status")
            }
        })

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        val busan = LatLng(35.1, 129.0)

        mMap.addMarker(MarkerOptions().position(busan).title("Marker in Busan"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(busan))
    }
}