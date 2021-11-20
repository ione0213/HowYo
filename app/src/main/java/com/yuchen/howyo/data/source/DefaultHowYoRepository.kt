package com.yuchen.howyo.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.remote.DeleteDataType
import com.yuchen.howyo.data.source.remote.HowYoRemoteDataSource
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType

class DefaultHowYoRepository(
    private val remoteDataSource: HowYoRemoteDataSource
) : HowYoRepository {
    override suspend fun signOut() {
        remoteDataSource.signOut()
    }

    override suspend fun createUser(user: User): Result<String> {
        return remoteDataSource.createUser(user)
    }

    override suspend fun getUser(userId: String): Result<User> {
        return remoteDataSource.getUser(userId)
    }

    override fun getLiveUser(userId: String): MutableLiveData<User> {
        return remoteDataSource.getLiveUser(userId)
    }

    override fun getLiveUsers(userIdList: List<String>): MutableLiveData<List<User>> {
        return remoteDataSource.getLiveUsers(userIdList)
    }

    override suspend fun getUsers(userIdList: List<String>): Result<List<User>> {
        return remoteDataSource.getUsers(userIdList)
    }

    override suspend fun updateUser(user: User): Result<Boolean> {
        return remoteDataSource.updateUser(user)
    }

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

    override fun getLivePlan(planId: String): MutableLiveData<Plan> {
        return remoteDataSource.getLivePlan(planId)
    }

    override suspend fun getPlans(authorList: List<String>): Result<List<Plan>> {
        return remoteDataSource.getPlans(authorList)
    }

    override suspend fun getAllPlans(): Result<List<Plan>> {
        return remoteDataSource.getAllPlans()
    }

    override suspend fun getAllPublicPlans(): Result<List<Plan>> {
        return remoteDataSource.getAllPublicPlans()
    }

    override fun getLivePlans(authorList: List<String>): MutableLiveData<List<Plan>> {
        return remoteDataSource.getLivePlans(authorList)
    }

    override fun getAllLivePublicPlans(): MutableLiveData<List<Plan>> {
        return remoteDataSource.getAllLivePublicPlans()
    }

    override fun getLivePublicPlans(authorList: List<String>): MutableLiveData<List<Plan>> {
        return remoteDataSource.getLivePublicPlans(authorList)
    }

    override fun getLiveCollectedPublicPlans(authorList: List<String>): MutableLiveData<List<Plan>> {
        return remoteDataSource.getLiveCollectedPublicPlans(authorList)
    }

    override suspend fun updatePlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.updatePlan(plan)
    }

    override suspend fun deletePlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.deletePlan(plan)
    }

    override suspend fun createDay(position: Int, planId: String): Result<Day> {
        return remoteDataSource.createDay(position, planId)
    }

    override suspend fun updateDay(day: Day): Result<Boolean> {
        return remoteDataSource.updateDay(day)
    }

    override suspend fun deleteDay(day: Day): Result<Boolean> {
        return remoteDataSource.deleteDay(day)
    }

    override suspend fun deleteDaysWithBatch(list: List<Day>): Result<Boolean> {
        return remoteDataSource.deleteDaysWithBatch(list)
    }

    override fun getLiveDays(planId: String): MutableLiveData<List<Day>> {
        return remoteDataSource.getLiveDays(planId)
    }

    override suspend fun getDays(planId: String): Result<List<Day>> {
        return remoteDataSource.getDays(planId)
    }

    override suspend fun createSchedule(schedule: Schedule): Result<Boolean> {
        return remoteDataSource.createSchedule(schedule)
    }

    override suspend fun createScheduleWithBatch(list: List<Schedule>): Result<Boolean> {
        return remoteDataSource.createScheduleWithBatch(list)
    }

    override suspend fun updateSchedule(schedule: Schedule): Result<Boolean> {
        return remoteDataSource.updateSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule): Result<Boolean> {
        return remoteDataSource.deleteSchedule(schedule)
    }

    override suspend fun deleteScheduleWithBatch(list: List<Schedule>): Result<Boolean> {
        return remoteDataSource.deleteScheduleWithBatch(list)
    }

    override fun getLiveSchedules(planId: String): MutableLiveData<List<Schedule>> {
        return remoteDataSource.getLiveSchedules(planId)
    }

    override suspend fun getSchedules(planId: String): Result<List<Schedule>> {
        return remoteDataSource.getSchedules(planId)
    }

    override suspend fun createCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean> {
        return remoteDataSource.createCheckShopList(checkShoppingList)
    }

    override suspend fun createCheckShopListWithBatch(list: List<CheckShoppingList>): Result<Boolean> {
        return remoteDataSource.createCheckShopListWithBatch(list)
    }

    override suspend fun updateCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean> {
        return remoteDataSource.updateCheckShopList(checkShoppingList)
    }

    override suspend fun deleteCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean> {
        return remoteDataSource.deleteCheckShopList(checkShoppingList)
    }

    override suspend fun deleteCheckShopListWithPlanID(planId: String): Result<Boolean> {
        return remoteDataSource.deleteCheckShopListWithPlanID(planId)
    }

    override fun getLiveCheckShopList(
        planId: String,
        mainType: MainItemType
    ): MutableLiveData<List<CheckShoppingList>> {
        return remoteDataSource.getLiveCheckShopList(planId, mainType)
    }

    override suspend fun createComment(comment: Comment): Result<Boolean> {
        return remoteDataSource.createComment(comment)
    }

    override suspend fun deleteComment(comment: Comment): Result<Boolean> {
        return remoteDataSource.deleteComment(comment)
    }

    override suspend fun deleteCommentWithBatch(list: List<Comment>): Result<Boolean> {
        return remoteDataSource.deleteCommentWithBatch(list)
    }

    override suspend fun getComments(planId: String): Result<List<Comment>> {
        return remoteDataSource.getComments(planId)
    }

    override fun getLiveComments(planId: String): MutableLiveData<List<Comment>> {
        return remoteDataSource.getLiveComments(planId)
    }

    override suspend fun createGroupMessage(groupMessage: GroupMessage): Result<Boolean> {
        return remoteDataSource.createGroupMessage(groupMessage)
    }

    override fun getLiveGroupMessages(planId: String): MutableLiveData<List<GroupMessage>> {
        return remoteDataSource.getLiveGroupMessages(planId)
    }

    override suspend fun createNotification(notification: Notification): Result<Boolean> {
        return remoteDataSource.createNotification(notification)
    }

    override fun getLiveNotifications(): MutableLiveData<List<Notification>> {
        return remoteDataSource.getLiveNotifications()
    }

    override suspend fun updateNotificationWithBatch(list: List<Notification>): Result<Boolean> {
        return remoteDataSource.updateNotificationWithBatch(list)
    }

    override suspend fun deleteFollowNotification(toUserId: String, fromUserId: String): Result<Boolean> {
        return remoteDataSource.deleteFollowNotification(toUserId, fromUserId)
    }

    override suspend fun createPayment(payment: Payment): Result<Boolean> {
        return remoteDataSource.createPayment(payment)
    }

    override suspend fun deletePayment(payment: Payment): Result<Boolean> {
        return remoteDataSource.deletePayment(payment)
    }

    override suspend fun updatePayment(payment: Payment): Result<Boolean> {
        return remoteDataSource.updatePayment(payment)
    }

    override fun getLivePayments(planId: String): MutableLiveData<List<Payment>> {
        return remoteDataSource.getLivePayments(planId)
    }

    override suspend fun deleteDataListsWithPlanID(planId: String, type: DeleteDataType): Result<Boolean> {
        return remoteDataSource.deleteDataListsWithPlanID(planId, type)
    }
}