package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.source.HowYoRepository

class ViewModelFactory constructor(
    private val howYoRepository: HowYoRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                //Sample
//                isAssignableFrom(CheckoutSuccessViewModel::class.java) ->
//                    CheckoutSuccessViewModel(stylishRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}