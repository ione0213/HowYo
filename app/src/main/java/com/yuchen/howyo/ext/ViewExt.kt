package com.yuchen.howyo.ext

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun View.closeKeyBoard() {
    val imm = this.let { ContextCompat.getSystemService(it.context, InputMethodManager::class.java) }
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}