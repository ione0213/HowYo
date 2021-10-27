package com.yuchen.howyo.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result

interface HowYoDataSource {

    suspend fun uploadPhoto(imgUri: Uri): Result<String>

    suspend fun createPlan(plan: Plan): Result<String>

    suspend fun getPlan(planId: String): Result<Plan>

    suspend fun createDay(position: Int, planId: String): Result<Boolean>

    fun getLiveDays(planId: String): MutableLiveData<List<Day>>

    suspend fun createMainCheckList(
        planId: String,
        mainType: String,
        subtype: String?
    ): Result<Boolean>
}