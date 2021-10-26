package com.yuchen.howyo.data.source.remote

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
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
}