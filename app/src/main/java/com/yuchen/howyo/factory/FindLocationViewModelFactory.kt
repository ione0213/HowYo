package com.yuchen.howyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.plan.findlocation.FindLocationViewModel

//class FindLocationViewModelFactory(
//    private val howYoRepository: HowYoRepository,
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>) =
//        with(modelClass) {
//            when {
//                isAssignableFrom(FindLocationViewModel::class.java) ->
//                    FindLocationViewModel(howYoRepository)
//                else ->
//                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//            }
//        } as T
//}