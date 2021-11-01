package com.yuchen.howyo.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.data.source.remote.HowYoRemoteDataSource

class DefaultHowYoRepository(
    private val remoteDataSource: HowYoRemoteDataSource
) : HowYoRepository {

    override suspend fun uploadPhoto(imgUri: Uri, fileName: String): Result<String> {
        return remoteDataSource.uploadPhoto(imgUri, fileName)
    }

    override suspend fun deletePhoto(fileName: String): Result<Boolean> {
        return remoteDataSource.deletePhoto(fileName)
    }

    override suspend fun createPlan(plan: Plan): Result<String> {
        return remoteDataSource.createPlan(plan)
    }

    override suspend fun getPlan(planId: String): Result<Plan> {
        return remoteDataSource.getPlan(planId)
    }

    override suspend fun updatePlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.updatePlan(plan)
    }

    override suspend fun deletePlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.deletePlan(plan)
    }

    override suspend fun createDay(position: Int, planId: String): Result<Boolean> {
        return remoteDataSource.createDay(position, planId)
    }

    override suspend fun updateDay(day: Day): Result<Boolean> {
        return remoteDataSource.updateDay(day)
    }

    override suspend fun deleteDay(day: Day): Result<Boolean> {
        return remoteDataSource.deleteDay(day)
    }

    override fun getLiveDays(planId: String): MutableLiveData<List<Day>> {
        return remoteDataSource.getLiveDays(planId)
    }

    override suspend fun createSchedule(schedule: Schedule): Result<Boolean> {
        return remoteDataSource.createSchedule(schedule)
    }

    override fun getLiveSchedules(planId: String): MutableLiveData<List<Schedule>> {
        return remoteDataSource.getLiveSchedules(planId)
    }

    override suspend fun createMainCheckList(
        planId: String,
        mainType: String,
        subtype: String?
    ): Result<Boolean> {
        return remoteDataSource.createMainCheckList(planId, mainType, subtype)
    }

    override suspend fun deleteMainCheckList(planId: String): Result<Boolean> {
        return remoteDataSource.deleteMainCheckList(planId)
    }

    override suspend fun deleteCheckList(planId: String): Result<Boolean> {
        return remoteDataSource.deleteCheckList(planId)
    }
}