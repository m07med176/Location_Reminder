package iti.android.gpslocation

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

data class Permissions(val permissionName: String, val requestCode:Int)
class PermissionUtils {
    companion object{
        const val PERMISSION_ID = 520

        @RequiresApi(Build.VERSION_CODES.N)
        fun List<Permissions>.permissionAction(activity: Activity, onPermissionGranted:()->Unit){
            if (checkPermission(activity))
                onPermissionGranted()
            else
                requestPermission(activity)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun List<Permissions>.checkPermission(activity: Activity):Boolean
        = stream().map { permission->
            ActivityCompat.checkSelfPermission( //ContextCompat
                activity.applicationContext,
                permission.permissionName
            ) == PackageManager.PERMISSION_GRANTED
        }.reduce{a,b -> a && b}.get()

        @RequiresApi(Build.VERSION_CODES.N)
        private fun List<Permissions>.requestPermission(activity: Activity) {
            stream().forEach {permission->
                ActivityCompat.requestPermissions(activity,
                    arrayOf(permission.permissionName),permission.requestCode)
            }

        }

        fun List<Permissions>.onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray, failerCallback:(permissionType:String)->Unit
        ) {
            forEach { permission ->
                if (requestCode == permission.requestCode && grantResults.size > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        failerCallback(permission.permissionName)
                    }
                }
            }

        }
    }
}