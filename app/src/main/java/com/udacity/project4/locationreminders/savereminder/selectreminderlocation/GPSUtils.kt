package iti.android.gpslocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import iti.android.gpslocation.PermissionUtils.Companion.permissionAction

class GPSUtils {
    companion object{
        val locationPermissionList = listOf(
            Permissions(Manifest.permission.ACCESS_FINE_LOCATION,254),
            Permissions(Manifest.permission.ACCESS_COARSE_LOCATION,254)

            )

        fun openGPSLocation(activity: Activity, callFuction:()->Unit) {
            locationPermissionList.permissionAction(activity){
                if (isLocationEnabled(activity)) {
                    // Logic on location enabled
                    callFuction()
                } else {
                    Toast.makeText(activity, "Please Turn On Location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    activity.startActivity(intent)
                }
            }

        }
        
        fun isLocationEnabled(context: Context): Boolean {
            var locationMode = 0
            val locationProviders: String
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                locationMode = try {
                    Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE)
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                    return false
                }
                locationMode != Settings.Secure.LOCATION_MODE_OFF
            } else {
                locationProviders = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED
                )
                !TextUtils.isEmpty(locationProviders)
            }
        }

        @SuppressLint("MissingPermission")
        fun requestNewLocationData(activity: Activity,onLocationResult:(location:Location)->Unit) {
            var mFusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
            val mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = 0
            mFusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        onLocationResult(locationResult.lastLocation!!)
                    }
                }, Looper.myLooper()
            )
        }

        fun getLastLocation(activity: Activity,onLocationResult:(location:Location)->Unit){
            openGPSLocation(activity){
                requestNewLocationData(activity,onLocationResult)
            }
        }
    }
}