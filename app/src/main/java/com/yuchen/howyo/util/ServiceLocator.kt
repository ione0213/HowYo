package com.yuchen.howyo.util

import androidx.annotation.VisibleForTesting
import com.yuchen.howyo.data.source.DefaultHowYoRepository
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.data.source.remote.HowYoRemoteDataSource

object ServiceLocator {

    @Volatile
    var howYoRepository: HowYoRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(): HowYoRepository {
        synchronized(this) {
            return howYoRepository
                ?: createHowYoRepository()
        }
    }

    private fun createHowYoRepository(): HowYoRepository {
        return DefaultHowYoRepository(HowYoRemoteDataSource)
    }
}
