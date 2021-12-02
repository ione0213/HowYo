package com.yuchen.howyo.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import kotlinx.coroutines.*

class SignInViewModel(private val howYoRepository: HowYoRepository) : ViewModel() {
    private val _createUserResult = MutableLiveData<String?>()

    val createUserResult: LiveData<String?>
        get() = _createUserResult

    private val _currentUser = MutableLiveData<User>()

    private val currentUser: LiveData<User>
        get() = _currentUser

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun createUser(user: User) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = howYoRepository.createUser(user)) {
                    is Result.Success -> {
                        user.id = result.data
                        _currentUser.postValue(user)
                        _createUserResult.postValue(result.data)
                    }
                    else -> {
                        _createUserResult.value = null
                    }
                }
            }
        }
    }

    fun setUser() {
        UserManager.apply {
            userId = currentUser.value?.id
            currentUserEmail = currentUser.value?.email
        }
    }
}
