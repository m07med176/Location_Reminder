package iti.android.gpslocation

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlin.streams.toList

data class Permissions(val permissionName: String, val requestCode:Int)
class PermissionUtils {
    companion object{
        const val PERMISSION_ID = 520

        fun List<Permissions>.permissionAction(activity: Activity,onPermissionGranted:()->Unit){
            if (checkPermission(activity))
                onPermissionGranted()
            else
                requestPermission(activity)
        }

        private fun List<Permissions>.checkPermission(activity: Activity):Boolean
         =  stream().map { permission->
            ActivityCompat.checkSelfPermission( //ContextCompat
                activity.applicationContext,
                permission.permissionName
            ) == PackageManager.PERMISSION_GRANTED
            }.toList().reduce{a,b -> a && b}


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