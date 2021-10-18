package com.yuchen.howyo.ext

import androidx.fragment.app.Fragment
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as HowYoApplication).howYoRepository
    return ViewModelFactory(repository)
}