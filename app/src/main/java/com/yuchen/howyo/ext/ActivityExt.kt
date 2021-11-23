package com.yuchen.howyo.ext

import android.app.Activity
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as HowYoApplication).howYoRepository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Activity.closeKeyBoard() {
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
}
