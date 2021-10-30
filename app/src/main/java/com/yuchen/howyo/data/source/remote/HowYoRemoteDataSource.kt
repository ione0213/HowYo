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
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HowYoRemoteDataSource : HowYoDataSource {

    private const val PATH_COVERS = "covers"
    private const val PATH_PLANS = "plans"
    private const val PATH_DAYS = "days"
    private const val PATH_CHECK_SHOPPING_LIST = "check_shopping_lists"
    private const val PATH_CHECK_ITEM_LIST = "check_item_lists"
    private const val KEY_POSITION = "position"

    override suspend fun uploadPhoto(imgUri: Uri, fileName: String): Result<String> =
        suspendCoroutine { continuation ->

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

    override suspend fun createMainCheckList(
        planId: String,
        mainType: String,
        subtype: String?
    ): Result<Boolean> =
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

    override suspend fun deleteMainCheckList(planId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val firebaseRef = FirebaseFirestore.getInstance()
            val collectionRef = firebaseRef.collection(PATH_CHECK_SHOPPING_LIST)

            val deleteResults = mutableListOf<Boolean>()

            collectionRef.whereEqualTo("plan_id", planId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.forEach { it ->
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

    override suspend fun deleteCheckList(planId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val firebaseRef = FirebaseFirestore.getInstance()
            val collectionRef = firebaseRef.collection(PATH_CHECK_ITEM_LIST)

            val deleteResults = mutableListOf<Boolean>()

            collectionRef.whereEqualTo("plan_id", planId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.forEach { it ->
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

                            Logger.w("[${this::class.simpleName}] Error deleting check list. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(HowYoApplication.instance.getString(R.string.nothing)))
                    }
                }
        }
}