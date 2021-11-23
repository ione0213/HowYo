package com.yuchen.howyo.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.yuchen.howyo.HowYoApplication

object Permission {

    fun isPermissionsAllowed(
        permissions: Array<String>,
        shouldRequestIfNotAllowed: Boolean = false,
        requestCode: Int = -1
    ): Boolean {
        var isGranted = true

        for (permission in permissions) {
            isGranted = ContextCompat.checkSelfPermission(
                HowYoApplication.instance,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted)
                break
        }
        if (!isGranted && shouldRequestIfNotAllowed) {
            if (requestCode == -1)
                throw RuntimeException("Send request code in third parameter")
            requestRequiredPermissions(permissions, requestCode)
        }

        return isGranted
    }

    private fun requestRequiredPermissions(permissions: Array<String>, requestCode: Int) {
        val pendingPermissions: ArrayList<String> = ArrayList()
        permissions.forEachIndexed { index, permission ->
            if (ContextCompat.checkSelfPermission(
                    HowYoApplication.instance,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            )
                pendingPermissions.add(permission)
        }
        val array = arrayOfNulls<String>(pendingPermissions.size)
        pendingPermissions.toArray(array)
        requestPermissions(Activity(), array, requestCode)
    }

    fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        var isGranted = true
        for (grantResult in grantResults) {
            isGranted = grantResult == PackageManager.PERMISSION_GRANTED
            if (!isGranted)
                break
        }
        return isGranted
    }
}
