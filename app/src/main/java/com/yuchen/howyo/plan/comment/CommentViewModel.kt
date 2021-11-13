package com.yuchen.howyo.plan.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.*
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.*

class CommentViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlan: Plan?
) : ViewModel() {

    private val plan = MutableLiveData<Plan>().apply {
        value = argumentPlan
    }

    private val _users = MutableLiveData<List<User>>()

    val user: LiveData<List<User>>
        get() = _users

    //All comments of plan
    var allComments = MutableLiveData<List<Comment>>()

    private val _commentData = MutableLiveData<MutableList<CommentData>>()

    val commentData: LiveData<MutableList<CommentData>>
        get() = _commentData

    var message = MutableLiveData<String>()

    private val _commentResult = MutableLiveData<Boolean>()

    val commentResult: LiveData<Boolean>
        get() = _commentResult

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

        getLiveCommentsResult()
    }

    private fun getLiveCommentsResult() {
        allComments = plan.value?.id?.let { howYoRepository.getLiveComments(it) }!!
    }

    fun getUsersResult() {

        val commentData = mutableListOf<CommentData>()

        coroutineScope.launch {

            withContext(Dispatchers.IO) {
                allComments.value?.forEach { comment ->
                    when (val result = comment.userId?.let { howYoRepository.getUser(it) }) {
                        is Result.Success -> {
                            commentData.add(
                                CommentData(
                                    comment.userId,
                                    result.data.avatar,
                                    comment.comment,
                                    comment.createdTime
                                )
                            )
                        }
                    }
                }

                _commentData.postValue(commentData)
            }
        }
    }

    fun submitComment() {

        val comment = Comment(
            planId = plan.value?.id,
            userId = UserManager.userId,
            comment = message.value
        )

        coroutineScope.launch {

            val result = howYoRepository.createComment(comment)

            _commentResult.value = when (result) {
                is Result.Success -> result.data
                else -> null
            }
        }
    }

    fun onSubmittedComment() {
        message.value = null
    }
}