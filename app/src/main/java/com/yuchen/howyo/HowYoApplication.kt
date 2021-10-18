package com.yuchen.howyo

import android.app.Application
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.util.ServiceLocator
import kotlin.properties.Delegates

class HowYoApplication: Application() {

    val howYoRepository: HowYoRepository
        get() = ServiceLocator.provideTasksRepository()

    companion object {
        var instance: HowYoApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}