package com.yuchen.howyo.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.remote.DeleteDataType
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType

interface HowYoRepository {

    suspend fun signOut()

    suspend fun createUser(user: User): Result<String>

    suspend fun getUser(userId: String): Result<User>

    fun getLiveUser(userId: String): MutableLiveData<User>

    fun getLiveUsers(userIdList: List<String>): MutableLiveData<List<User>>

    suspend fun getUsers(userIdList: List<String>): Result<List<User>>

    suspend fun updateUser(user: User): Result<Boolean>

    suspend fun uploadPhoto(imgUri: Uri, fileName: String): Result<String>

    suspend fun deletePhoto(fileName: String): Result<Boolean>

    suspend fun createPlan(plan: Plan): Result<String>

    suspend fun getPlan(planId: String): Result<Plan>

    fun getLivePlan(planId: String): MutableLiveData<Plan>

    suspend fun getPlans(authorList: List<String>): Result<List<Plan>>

    suspend fun getAllPlans(): Result<List<Plan>>

    suspend fun getAllPublicPlans(): Result<List<Plan>>

    fun getLivePlans(authorList: List<String>): MutableLiveData<List<Plan>>

    fun getAllLivePublicPlans(): MutableLiveData<List<Plan>>

    fun getLivePublicPlans(authorList: List<String>): MutableLiveData<List<Plan>>

    fun getLiveCollectedPublicPlans(authorList: List<String>): MutableLiveData<List<Plan>>

    suspend fun updatePlan(plan: Plan): Result<Boolean>

    suspend fun deletePlan(plan: Plan): Result<Boolean>

    suspend fun createDay(position: Int, planId: String): Result<Day>

    suspend fun updateDay(day: Day): Result<Boolean>

    suspend fun deleteDay(day: Day): Result<Boolean>

    suspend fun deleteDaysWithBatch(list: List<Day>): Result<Boolean>

    fun getLiveDays(planId: String): MutableLiveData<List<Day>>

    suspend fun getDays(planId: String): Result<List<Day>>

    suspend fun createSchedule(schedule: Schedule): Result<Boolean>

    suspend fun createScheduleWithBatch(list: List<Schedule>): Result<Boolean>

    suspend fun updateSchedule(schedule: Schedule): Result<Boolean>

    suspend fun deleteSchedule(schedule: Schedule): Result<Boolean>

    suspend fun deleteScheduleWithBatch(list: List<Schedule>): Result<Boolean>

    fun getLiveSchedules(planId: String): MutableLiveData<List<Schedule>>

    suspend fun getSchedules(planId: String): Result<List<Schedule>>

    suspend fun createCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean>

    suspend fun createCheckShopListWithBatch(list: List<CheckShoppingList>): Result<Boolean>

    suspend fun updateCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean>

    suspend fun deleteCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean>

    suspend fun deleteCheckShopListWithPlanID(planId: String): Result<Boolean>

    fun getLiveCheckShopList(planId: String, mainType: MainItemType): MutableLiveData<List<CheckShoppingList>>

    suspend fun createComment(comment: Comment): Result<Boolean>

    suspend fun deleteComment(comment: Comment): Result<Boolean>

    suspend fun deleteCommentWithBatch(list: List<Comment>): Result<Boolean>

    suspend fun getComments(planId: String): Result<List<Comment>>

    fun getLiveComments(planId: String): MutableLiveData<List<Comment>>

    suspend fun createGroupMessage(groupMessage: GroupMessage): Result<Boolean>

    fun getLiveGroupMessages(planId: String): MutableLiveData<List<GroupMessage>>

    suspend fun createNotification(notification: Notification): Result<Boolean>

    fun getLiveNotifications(): MutableLiveData<List<Notification>>

    suspend fun updateNotificationWithBatch(list: List<Notification>): Result<Boolean>

    suspend fun deleteFollowNotification(toUserId: String, fromUserId: String): Result<Boolean>

    suspend fun createPayment(payment: Payment): Result<Boolean>

    suspend fun deletePayment(payment: Payment): Result<Boolean>

    suspend fun updatePayment(payment: Payment): Result<Boolean>

    fun getLivePayments(planId: String): MutableLiveData<List<Payment>>

    suspend fun deleteDataListsWithPlanID(planId: String, type: DeleteDataType): Result<Boolean>
}
