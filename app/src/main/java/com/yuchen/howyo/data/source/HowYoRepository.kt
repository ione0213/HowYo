package com.yuchen.howyo.data.source

import android.net.Uri
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result

interface HowYoRepository {

    suspend fun uploadPhoto(imgUri: Uri): Result<String>

    suspend fun createPlan(plan: Plan): Result<String>

    suspend fun getPlan(planId: String): Result<Plan>
}