package com.yuchen.howyo.data.source

import android.net.Uri
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.source.remote.HowYoRemoteDataSource

class DefaultHowYoRepository(
    private val remoteDataSource: HowYoRemoteDataSource
) : HowYoRepository {

    override suspend fun uploadPhoto(imgUri: Uri): Result<String> {
        return remoteDataSource.uploadPhoto(imgUri)
    }

    override suspend fun createPlan(plan: Plan): Result<String> {
        return remoteDataSource.createPlan(plan)
    }

    override suspend fun getPlan(planId: String): Result<Plan> {
        return remoteDataSource.getPlan(planId)
    }
}