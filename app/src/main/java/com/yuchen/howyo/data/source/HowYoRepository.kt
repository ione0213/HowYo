package com.yuchen.howyo.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.yuchen.howyo.data.*

interface HowYoRepository {

    suspend fun createUser(user: User): Result<String>

    suspend fun getUser(userId: String): Result<User>

    fun getLiveUser(email: String): MutableLiveData<User>

    suspend fun uploadPhoto(imgUri: Uri, fileName: String): Result<String>

    suspend fun deletePhoto(fileName: String): Result<Boolean>

    suspend fun createPlan(plan: Plan): Result<String>

    suspend fun getPlan(planId: String): Result<Plan>

    fun getLivePlan(planId: String): MutableLiveData<Plan>

    fun getLivePlans(authorList: List<String>): MutableLiveData<List<Plan>>

    suspend fun updatePlan(plan: Plan): Result<Boolean>

    suspend fun deletePlan(plan: Plan): Result<Boolean>

    suspend fun createDay(position: Int, planId: String): Result<Boolean>

    suspend fun updateDay(day: Day): Result<Boolean>

    suspend fun deleteDay(day: Day): Result<Boolean>

    fun getLiveDays(planId: String): MutableLiveData<List<Day>>

    suspend fun getDays(planId: String): Result<List<Day>>

    suspend fun createSchedule(schedule: Schedule): Result<Boolean>

    suspend fun updateSchedule(schedule: Schedule): Result<Boolean>

    suspend fun deleteSchedule(schedule: Schedule): Result<Boolean>

    fun getLiveSchedules(planId: String): MutableLiveData<List<Schedule>>

    suspend fun getSchedules(planId: String): Result<List<Schedule>>

    suspend fun createMainCheckList(
        planId: String,
        mainType: String,
        subtype: String?
    ): Result<Boolean>

    suspend fun deleteMainCheckList(planId: String): Result<Boolean>

    suspend fun deleteCheckList(planId: String): Result<Boolean>

    suspend fun createComment(comment: Comment): Result<Boolean>

    suspend fun deleteComment(comment: Comment): Result<Boolean>

    suspend fun getComments(planId: String): Result<List<Comment>>

    fun getLiveComments(planId: String): MutableLiveData<List<Comment>>
}