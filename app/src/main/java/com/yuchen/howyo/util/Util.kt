package com.yuchen.howyo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.yuchen.howyo.HowYoApplication

/**
 * Updated by Wayne Chen in Mar. 2019.
 */
object Util {

    fun isInternetConnected(): Boolean {
        val cm = HowYoApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return HowYoApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return HowYoApplication.instance.getColor(resourceId)
    }
}
