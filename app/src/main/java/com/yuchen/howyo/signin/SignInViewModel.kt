package com.yuchen.howyo.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SignInViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {

    private val _createUserResult = MutableLiveData<Boolean>()

    val createUserResult: LiveData<Boolean>
        get() = _createUserResult

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setUser(user: User) {

        UserManager.currentUserEmail = user.email

        coroutineScope.launch {
            _createUserResult.value = when (val result = howYoRepository.createUser(user)) {
                is Result.Success -> result.data
                else -> false
            }
        }

    }
}