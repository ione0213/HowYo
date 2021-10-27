package com.yuchen.howyo.data.source.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.CheckShoppingList
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.source.HowYoDataSource
import com.yuchen.howyo.util.Logger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HowYoRemoteDataSource : HowYoDataSource {

    private const val PATH_COVERS = "covers"
    private const val PATH_PLANS = "plans"
    private const val PATH_DAYS = "days"
    private const val PATH_CHECK_SHOPPING_LIST = "check_shopping_lists"
    private const val KEY_POSITION = "position"

    override suspend fun uploadPhoto(imgUri: Uri): Result<String> =
        suspendCoroutine { continuation ->
            val formatter = SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
            val fileName = "cover_${formatter.format(Date())}"
            val storageRef =
                FirebaseStorage.getInstance().reference.child("$PATH_COVERS/$fileName")

            storageRef.putFile(imgUri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.storage.downloadUrl.addOnCompleteListener { it ->
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

    override fun getLiveDays(planId: String): MutableLiveData<List<Day>> {

        val liveData = MutableLiveData<List<Day>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_DAYS)
            .whereEqualTo("plan_id", planId)
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

    override suspend fun createMainCheckList(planId: String, mainType: String, subtype: String?): Result<Boolean> =
        suspendCoroutine { continuation ->
            val mainCheckListRef = FirebaseFirestore.getInstance().collection(
                PATH_CHECK_SHOPPING_LIST
            )

            val document = mainCheckListRef.document()

            val mainCheckList = CheckShoppingList(
                id = document.id,
                planId = planId,
                mainType = mainType,
                subType = subtype
            )

            document
                .set(mainCheckList)
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
}