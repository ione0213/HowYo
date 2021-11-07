package com.yuchen.howyo.signin

import android.content.Context
import com.yuchen.howyo.HowYoApplication

/**
 * Created by Wayne Chen in Jul. 2019｀.
 */
object UserManager {

    private const val USER_DATA = "user_data"
    private const val CURRENT_USER = "current_user"

    var currentUserEmail: String? = null
        get() = HowYoApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(CURRENT_USER, null)
        set(value) {
            field = when (value) {
                null -> {
                    HowYoApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(CURRENT_USER)
                        .apply()
                    null
                }
                else -> {
                    HowYoApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(CURRENT_USER, value)
                        .apply()
                    value
                }
            }
        }

    val isLoggedIn: Boolean
        get() = currentUserEmail != null

    fun clear() {
        currentUserEmail = null
    }
}
