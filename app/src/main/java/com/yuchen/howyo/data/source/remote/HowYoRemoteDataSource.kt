package com.yuchen.howyo.data.source.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.HowYoDataSource
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType
import com.yuchen.howyo.util.Logger
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HowYoRemoteDataSource : HowYoDataSource {

    private const val PATH_COVERS = "images"
    private const val PATH_USERS = "users"
    private const val PATH_PLANS = "plans"
    private const val PATH_DAYS = "days"
    private const val PATH_SCHEDULES = "schedules"
    private const val PATH_CHECK_SHOPPING_LIST = "check_shopping_lists"
    private const val PATH_COMMENTS = "comments"
    private const val KEY_POSITION = "position"
    private const val KEY_CREATED_TIME = "created_time"
    private const val KEY_EMAIL = "email"
    private const val KEY_ID = "id"
    private const val KEY_AUTHOR_ID = "author_id"
    private const val KEY_PRIVACY = "privacy"
    private const val KEY_COLLECTED = "plan_collected_list"
    private const val KEY_PLAN_ID = "plan_id"
    private const val KEY_MAIN_TYPE = "main_type"

    override suspend fun createUser(user: User): Result<String> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_USERS)
                .whereEqualTo(KEY_EMAIL, user.email)
                .get()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        when {
                            task.result.size() == 0 -> {
                                val users = FirebaseFirestore.getInstance().collection(PATH_USERS)
                                val document = users.document()

                                user.id = document.id

                                document
                                    .set(user)
                                    .addOnCompleteListener { createUserTask ->
                                        if (createUserTask.isSuccessful) {
                                            continuation.resume(Result.Success(user.id!!))
                                        } else {
                                            createUserTask.exception?.let {
                                                Logger.w("[${this::class.simpleName}] Error creating user. ${it.message}")
                                                continuation.resume(Result.Error(it))
                                                return@let
                                            }
                                            continuation.resume(
                                                Result.Fail(HowYoApplication.instance.getString(R.string.nothing))
                                            )
                                        }
                                    }
                            }
                            task.result.size() > 0 -> {
                                continuation.resume(Result.Success(task.result.first().id))
                            }
                        }
                    } else {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error creating user. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }

                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }
        }

    override suspend fun getUser(userId: String): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .whereEqualTo(KEY_ID, userId)
            .get()
            .addOnCompleteListener { task ->
                val user: User

                if (task.isSuccessful) {
                    if (task.result != null) {
                        user = task.result.first().toObject(User::class.java)
                        continuation.resume(Result.Success(user))
                    }
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting user. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }

                    continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                }
            }
    }

    override fun getLiveUser(userId: String): MutableLiveData<User> {

        val liveData = MutableLiveData<User>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USERS)
            .whereEqualTo(KEY_ID, userId)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener live user detect:$userId")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting user. ${it.message}")
                }

                if (snapshot != null) {
                    when {
                        snapshot.size() > 0 -> {
                            liveData.value = snapshot.first().toObject(User::class.java)
                        }
                    }
                }
            }

        return liveData
    }

    override suspend fun updateUser(user: User): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_USERS)
                .document(user.id)
                .set(user)
                .addOnSuccessListener {
                    Logger.i("Update: $user")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error updating user. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun uploadPhoto(imgUri: Uri, fileName: String): Result<String> =
        suspendCoroutine { continuation ->

            val storageRef =
                FirebaseStorage.getInstance().reference.child("$PATH_COVERS/$fileName")

            storageRef.putFile(imgUri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.storage.downloadUrl.addOnCompleteListener {
                            if (it.isSuccessful) {
                                continuation.resume(Result.Success(it.result.toString()))
                            } else {
                                task.exception?.let { exception ->

                                    Logger.w("[${this::class.simpleName}] Error uploading img. ${exception.message}")
                                    continuation.resume(Result.Error(exception))
                                    return@let
                                }
                                continuation.resume(
                                    Result.Fail(HowYoApplication.instance.getString(R.string.nothing))
                                )
                            }
                        }
                    } else {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error uploading img. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(HowYoApplication.instance.getString(R.string.nothing))
                        )
                    }
                }
        }

    override suspend fun deletePhoto(fileName: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseStorage.getInstance().reference
                .child("$PATH_COVERS/$fileName")
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error deleting img. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(HowYoApplication.instance.getString(R.string.nothing))
                        )
                    }
                }
        }

    override suspend fun createPlan(plan: Plan): Result<String> = suspendCoroutine { continuation ->
        val plans = FirebaseFirestore.getInstance().collection(PATH_PLANS)
        val document = plans.document()

        plan.id = document.id

        document
            .set(plan)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.Success(document.id))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error creating plan. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(
                        Result.Fail(HowYoApplication.instance.getString(R.string.nothing))
                    )
                }
            }
    }

    override suspend fun getPlan(planId: String): Result<Plan> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_PLANS)
            .whereEqualTo("id", planId)
            .get()
            .addOnCompleteListener { task ->
                val plan: Plan

                if (task.isSuccessful) {
                    plan = task.result.first().toObject(Plan::class.java)
                    continuation.resume(Result.Success(plan))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }

                    continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                }
            }
    }

    override fun getLivePlan(planId: String): MutableLiveData<Plan> {

        val liveData = MutableLiveData<Plan>()

        FirebaseFirestore.getInstance()
            .collection(PATH_PLANS)
            .whereEqualTo("id", planId)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener live plan detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting plan. ${it.message}")
                }

                if (snapshot != null) {
                    if (snapshot.size() != 0) {
                        liveData.value = snapshot.first().toObject(Plan::class.java)
                    }
                }
            }

        return liveData
    }

    override suspend fun getPlans(authorList: List<String>): Result<List<Plan>> =
        suspendCoroutine { continuation ->
            when (authorList.size) {
                0 -> {
                    continuation.resume(Result.Success(listOf()))
                }
                else -> {
                    FirebaseFirestore.getInstance()
                        .collection(PATH_PLANS)
                        .whereIn(KEY_AUTHOR_ID, authorList)
                        .whereEqualTo(KEY_PRIVACY, "public")
                        .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                val list = mutableListOf<Plan>()

                                if (task.result != null) {
                                    for (document in task.result!!) {
                                        Logger.d(document.id + " => " + document.data)

                                        val plan = document.toObject(Plan::class.java)
                                        list.add(plan)
                                    }
                                    continuation.resume(Result.Success(list))
                                }
                            } else {
                                task.exception?.let {

                                    Logger.w("[${this::class.simpleName}] Error getting schedules. ${it.message}")
                                    continuation.resume(Result.Error(it))
                                    return@addOnCompleteListener
                                }
                                continuation.resume(
                                    Result.Fail(
                                        HowYoApplication.instance.getString(
                                            R.string.nothing
                                        )
                                    )
                                )
                            }
                        }
                }
            }
        }

    override fun getLivePlans(authorList: List<String>): MutableLiveData<List<Plan>> {

        val liveData = MutableLiveData<List<Plan>>()
        when (authorList.size) {
            0 -> {

            }
            else -> {
                FirebaseFirestore.getInstance()
                    .collection(PATH_PLANS)
                    .whereIn(KEY_AUTHOR_ID, authorList)
                    .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, exception ->

                        Logger.i("addSnapshotListener detect")

                        exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting plans. ${it.message}")
                        }

                        val list = mutableListOf<Plan>()
                        if (snapshot != null) {
                            for (document in snapshot) {
                                Logger.d(document.id + " => " + document.data)

                                val plan = document.toObject(Plan::class.java)
                                list.add(plan)
                            }
                        }

                        liveData.value = list
                    }
            }
        }

        return liveData
    }

    override fun getAllLivePublicPlans(): MutableLiveData<List<Plan>> {

        val liveData = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLANS)
            .whereEqualTo(KEY_PRIVACY, "public")
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting plans. ${it.message}")
                }

                val list = mutableListOf<Plan>()
                if (snapshot != null) {
                    for (document in snapshot) {
                        Logger.d(document.id + " => " + document.data)

                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                }

                liveData.value = list
            }

        return liveData
    }

    override fun getLivePublicPlans(authorList: List<String>): MutableLiveData<List<Plan>> {

        val liveData = MutableLiveData<List<Plan>>()
        when (authorList.size) {
            0 -> {

            }
            else -> {
                FirebaseFirestore.getInstance()
                    .collection(PATH_PLANS)
                    .whereIn(KEY_AUTHOR_ID, authorList)
                    .whereEqualTo(KEY_PRIVACY, "public")
                    .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, exception ->

                        Logger.i("addSnapshotListener detect")

                        exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting plans. ${it.message}")
                        }

                        val list = mutableListOf<Plan>()
                        if (snapshot != null) {
                            for (document in snapshot) {
                                Logger.d(document.id + " => " + document.data)

                                val plan = document.toObject(Plan::class.java)
                                list.add(plan)
                            }
                        }

                        liveData.value = list
                    }
            }
        }

        return liveData
    }

    override fun getLiveCollectedPublicPlans(authorList: List<String>): MutableLiveData<List<Plan>> {

        val liveData = MutableLiveData<List<Plan>>()
        when (authorList.size) {
            0 -> {

            }
            else -> {
                FirebaseFirestore.getInstance()
                    .collection(PATH_PLANS)
                    .whereArrayContainsAny(KEY_COLLECTED, authorList)
                    .whereEqualTo(KEY_PRIVACY, "public")
                    .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, exception ->

                        Logger.i("addSnapshotListener detect")

                        exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting plans. ${it.message}")
                        }

                        val list = mutableListOf<Plan>()
                        if (snapshot != null) {
                            for (document in snapshot) {
                                Logger.d(document.id + " => " + document.data)

                                val plan = document.toObject(Plan::class.java)
                                list.add(plan)
                            }
                        }

                        liveData.value = list
                    }
            }
        }

        return liveData
    }

    override suspend fun updatePlan(plan: Plan): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_PLANS)
                .document(plan.id)
                .set(plan)
                .addOnSuccessListener {
                    Logger.i("Update: $plan")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error updating plan. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun deletePlan(plan: Plan): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_PLANS)
                .document(plan.id)
                .delete()
                .addOnSuccessListener {
                    Logger.i("Delete: $plan")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error deleting plan. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun createDay(position: Int, planId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            val dayRef = FirebaseFirestore.getInstance().collection(PATH_DAYS)
            val document = dayRef.document()

            val day = Day(
                document.id,
                planId,
                position = position
            )

            document
                .set(day)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error creating day. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }
        }

    override suspend fun updateDay(day: Day): Result<Boolean> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_DAYS)
            .document(day.id)
            .set(day)
            .addOnSuccessListener {
                Logger.i("Update: $day")

                continuation.resume(Result.Success(true))
            }.addOnFailureListener {
                Logger.w("[${this::class.simpleName}] Error updating day. ${it.message}")
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun deleteDay(day: Day): Result<Boolean> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_DAYS)
            .document(day.id)
            .delete()
            .addOnSuccessListener {
                Logger.i("Delete: $day")

                continuation.resume(Result.Success(true))
            }.addOnFailureListener {
                Logger.w("[${this::class.simpleName}] Error deleting day. ${it.message}")
                continuation.resume(Result.Error(it))
            }
    }

    override fun getLiveDays(planId: String): MutableLiveData<List<Day>> {

        val liveData = MutableLiveData<List<Day>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_DAYS)
            .whereEqualTo(KEY_PLAN_ID, planId)
            .orderBy(KEY_POSITION, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting days. ${it.message}")
                }

                val list = mutableListOf<Day>()
                for (document in snapshot!!) {
                    Logger.d(document.id + " => " + document.data)

                    val day = document.toObject(Day::class.java)
                    list.add(day)
                }

                liveData.value = list
            }

        return liveData
    }

    override suspend fun getDays(planId: String): Result<List<Day>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_DAYS)
                .whereEqualTo(KEY_PLAN_ID, planId)
                .orderBy(KEY_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Day>()
                        for (document in task.result!!) {
                            Logger.d(document.id + " => " + document.data)

                            val day = document.toObject(Day::class.java)
                            list.add(day)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting days. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }
        }

    override suspend fun createSchedule(schedule: Schedule): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_SCHEDULES)
                .whereEqualTo("day_id", schedule.dayId)
                .orderBy(KEY_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { task ->
                    val lastSchedule: Schedule

                    if (task.isSuccessful) {
                        when (task.result.size()) {
                            0 -> {
                                schedule.position = 0
                            }
                            else -> {
                                lastSchedule = task.result.last().toObject(Schedule::class.java)
                                schedule.position = lastSchedule.position?.plus(1)
                            }
                        }

                        val schedules = FirebaseFirestore.getInstance().collection(PATH_SCHEDULES)
                        val document = schedules.document()

                        schedule.id = document.id

                        document
                            .set(schedule)
                            .addOnCompleteListener { scheduleTask ->
                                if (scheduleTask.isSuccessful) {

                                    continuation.resume(Result.Success(true))
                                } else {
                                    scheduleTask.exception?.let {
                                        Logger.w("[${this::class.simpleName}] Error creating schedule. ${it.message}")
                                        continuation.resume(Result.Error(it))
                                        return@let
                                    }
                                    continuation.resume(
                                        Result.Fail(HowYoApplication.instance.getString(R.string.nothing))
                                    )
                                }
                            }
                    } else {
                        continuation.resume(Result.Success(false))
                    }
                }
        }

    override suspend fun updateSchedule(schedule: Schedule): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_SCHEDULES)
                .document(schedule.id!!)
                .set(schedule)
                .addOnSuccessListener {
                    Logger.i("Update: $schedule")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error updating schedule. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun deleteSchedule(schedule: Schedule): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_SCHEDULES)
                .document(schedule.id!!)
                .delete()
                .addOnSuccessListener {
                    Logger.i("Delete: $schedule")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error deleting schedule. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override fun getLiveSchedules(planId: String): MutableLiveData<List<Schedule>> {

        val liveData = MutableLiveData<List<Schedule>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_SCHEDULES)
            .whereEqualTo(KEY_PLAN_ID, planId)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting schedules. ${it.message}")
                }

                val list = mutableListOf<Schedule>()
                if (snapshot != null) {
                    for (document in snapshot) {

                        val schedule = document.toObject(Schedule::class.java)
                        list.add(schedule)
                    }
                }

                liveData.value = list
            }

        return liveData
    }

    override suspend fun getSchedules(planId: String): Result<List<Schedule>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_SCHEDULES)
                .whereEqualTo(KEY_PLAN_ID, planId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Schedule>()
                        for (document in task.result!!) {
                            Logger.d(document.id + " => " + document.data)

                            val schedule = document.toObject(Schedule::class.java)
                            list.add(schedule)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting schedules. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }
        }

    override suspend fun createCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean> =
        suspendCoroutine { continuation ->
            val mainCheckListRef = FirebaseFirestore.getInstance().collection(
                PATH_CHECK_SHOPPING_LIST
            )

            val document = mainCheckListRef.document()

            checkShoppingList.id = document.id

            document
                .set(checkShoppingList)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error creating main check list. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }

        }

    override suspend fun updateCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_CHECK_SHOPPING_LIST)
                .document(checkShoppingList.id)
                .set(checkShoppingList)
                .addOnSuccessListener {
                    Logger.i("Update: $checkShoppingList")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error updating schedule. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun deleteCheckShopList(checkShoppingList: CheckShoppingList): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_CHECK_SHOPPING_LIST)
                .document(checkShoppingList.id)
                .delete()
                .addOnSuccessListener {
                    Logger.i("Delete: $checkShoppingList")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error deleting checkShoppingList. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun deleteCheckShopListWithPlanID(planId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val firebaseRef = FirebaseFirestore.getInstance()
            val collectionRef = firebaseRef.collection(PATH_CHECK_SHOPPING_LIST)

            val deleteResults = mutableListOf<Boolean>()

            collectionRef.whereEqualTo(KEY_PLAN_ID, planId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.forEach {
                            collectionRef.document(it.id).delete()
                                .addOnCompleteListener { subTask ->
                                    if (subTask.isSuccessful) {
                                        deleteResults.add(true)
                                    } else {
                                        deleteResults.add(false)
                                    }
                                }
                        }

                        continuation.resume(
                            when (!deleteResults.contains(false)) {
                                true -> Result.Success(true)
                                false -> Result.Success(false)
                            }
                        )
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error deleting main check list. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }
        }

    override fun getLiveCheckShopList(
        planId: String,
        mainType: MainItemType
    ): MutableLiveData<List<CheckShoppingList>> {

        val liveData = MutableLiveData<List<CheckShoppingList>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_CHECK_SHOPPING_LIST)
            .whereEqualTo(KEY_PLAN_ID, planId)
            .whereEqualTo(KEY_MAIN_TYPE, mainType.value)
            .orderBy(KEY_CREATED_TIME, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting comments. ${it.message}")
                }

                val list = mutableListOf<CheckShoppingList>()

                if (snapshot != null) {
                    for (document in snapshot) {
                        Logger.d(document.id + " => " + document.data)

                        val checkShoppingList = document.toObject(CheckShoppingList::class.java)
                        list.add(checkShoppingList)
                    }
                }
                liveData.value = list
            }

        return liveData
    }

    override suspend fun createComment(comment: Comment): Result<Boolean> =
        suspendCoroutine { continuation ->
            val comments = FirebaseFirestore.getInstance().collection(PATH_COMMENTS)
            val document = comments.document()

            comment.id = document.id

            document
                .set(comment)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error creating comment. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(HowYoApplication.instance.getString(R.string.nothing))
                        )
                    }
                }
        }

    override suspend fun deleteComment(comment: Comment): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collection(PATH_COMMENTS)
                .document(comment.id)
                .delete()
                .addOnSuccessListener {
                    Logger.i("Delete: $comment")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error deleting comment. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun getComments(planId: String): Result<List<Comment>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_COMMENTS)
                .whereEqualTo(KEY_PLAN_ID, planId)
                .orderBy(KEY_POSITION, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Comment>()
                        for (document in task.result!!) {
                            Logger.d(document.id + " => " + document.data)

                            val comment = document.toObject(Comment::class.java)
                            list.add(comment)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting days. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }
        }

    override fun getLiveComments(planId: String): MutableLiveData<List<Comment>> {
        val liveData = MutableLiveData<List<Comment>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_COMMENTS)
            .whereEqualTo(KEY_PLAN_ID, planId)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting comments. ${it.message}")
                }

                val list = mutableListOf<Comment>()

                if (snapshot != null) {
                    for (document in snapshot) {
                        Logger.d(document.id + " => " + document.data)

                        val comment = document.toObject(Comment::class.java)
                        list.add(comment)
                    }
                }
                liveData.value = list
            }

        return liveData
    }
}