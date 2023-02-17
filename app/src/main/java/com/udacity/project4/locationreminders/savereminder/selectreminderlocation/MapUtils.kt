package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import java.util.*

private const val TAG = "MapUtils"
class MapUtils {
    companion object{
        fun setPoiClick(map: GoogleMap) {
            map.setOnPoiClickListener { poi ->
                map.clear()
                val marker = map.addMarker(
                    MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                )
                marker?.showInfoWindow()
                map.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng))
            }
        }

        fun setMapLongClick(map: GoogleMap,context: Context) {
            map.setOnMapLongClickListener { latLng ->
                val snippet = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    latLng.latitude,
                    latLng.longitude
                )
                map.clear()
                var marker = map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(context.getString(R.string.dropped_pin))
                        .snippet(snippet)
                )

                marker?.showInfoWindow()
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }


        fun setMapStyle(map: GoogleMap,context: Context) {
            try {
                val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        R.raw.map_style
                    )
                )
                if (!success) {
                    Log.e(TAG, "failed")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e(TAG, "Error:$e")
            }
        }

    }
}
