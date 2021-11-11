package com.yuchen.howyo.plan.checkorshoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.CheckShoppingList
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.plan.CheckItemType
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.Util.getString
import kotlinx.coroutines.*

class CheckOrShoppingListViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlanId: String,
    private val argumentMainType: MainItemType
) : ViewModel() {

    //Handle all check lists
    var allCheckLists = MutableLiveData<List<CheckShoppingList>>()

    val planId: String
        get() = argumentPlanId

    val mainType: MainItemType
        get() = argumentMainType

    val item = MutableLiveData<String>()

    private val _isItemCreated = MutableLiveData<Boolean>()

    val isItemCreated: LiveData<Boolean>
        get() = _isItemCreated

    private val _isResetItem = MutableLiveData<Boolean>()

    val isResetItem: LiveData<Boolean>
        get() = _isResetItem

    private val _isChkListDeleted = MutableLiveData<Boolean>()

    private val isChkListDeleted: LiveData<Boolean>
        get() = _isChkListDeleted

    private val _isChkListReady = MutableLiveData<Boolean>()

    private val isChkListReady: LiveData<Boolean>
        get() = _isChkListReady

    private val _isAllDataReady = MutableLiveData<Boolean>()

    val isAllDataReady: LiveData<Boolean>
        get() = _isAllDataReady

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        getCheckShoppingListResult()
    }

    private fun getCheckShoppingListResult() {
        allCheckLists = howYoRepository.getLiveCheckShopList(planId, mainType)
    }

    fun createItem(subType: String) {

        val item = CheckShoppingList(
            planId = planId,
            mainType = mainType.value,
            subType = subType,
            item = this.item.value
        )

        coroutineScope.launch {

            val result = howYoRepository.createCheckShopList(item)

            _isItemCreated.value = when (result) {
                is Result.Success -> {
                    result.data
                }
                else -> {
                    null
                }
            }
        }
    }

    fun onCreatedItem() {
        item.value = ""
    }

    fun deleteItem(item: CheckShoppingList) {

        coroutineScope.launch {
            howYoRepository.deleteCheckShopList(item)
        }
    }

    fun resetItem() {
        _isResetItem.value = true
    }

    fun onResetItem() {
        _isResetItem.value = null
        _status.value = LoadApiStatus.DONE
    }

    fun resetCheckList() {

        _status.value = LoadApiStatus.LOADING

        coroutineScope.launch {

            withContext(Dispatchers.IO) {

                _isChkListDeleted.postValue(deleteAllCheckShopList())
                _isChkListReady.postValue(createDefaultCheckList())
            }

            when {
                isChkListDeleted.value == true && isChkListReady.value == true -> {
                    _isAllDataReady.value = true
                }
            }
        }


    }

    private suspend fun deleteAllCheckShopList(): Boolean {

        val deleteResult = mutableListOf<Boolean>()

        allCheckLists.value?.forEach {
            when (howYoRepository.deleteCheckShopList(it)) {
                is Result.Success -> deleteResult.add(true)
                else -> deleteResult.add(false)
            }
        }

        return !deleteResult.contains(false)
    }

    private suspend fun createDefaultCheckList(): Boolean {

        val planId = planId
        val subTypeList = HowYoApplication.instance.resources.getStringArray(R.array.check_list)
        val checkListResults = mutableListOf<Boolean>()

        subTypeList.forEach { subType ->
            when (subType) {
                getString(R.string.necessary) -> {
                    CheckItemType.NECESSARY.list.forEach { item ->

                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
                    }
                }
                getString(R.string.clothe) -> {
                    CheckItemType.CLOTHE.list.forEach { item ->

                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
                    }
                }
                getString(R.string.wash) -> {
                    CheckItemType.WASH.list.forEach { item ->

                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
                    }
                }
                getString(R.string.electronic) -> {
                    CheckItemType.ELECTRONIC.list.forEach { item ->

                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
                    }
                }
                getString(R.string.health) -> {
                    CheckItemType.HEALTH.list.forEach { item ->

                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
                    }
                }
                getString(R.string.other) -> {
                    CheckItemType.OTHER.list.forEach { item ->

                        val newItem = CheckShoppingList(
                            planId = planId,
                            mainType = getString(R.string.check),
                            subType = subType,
                            item = item
                        )

                        val result = howYoRepository.createCheckShopList(newItem)

                        if (result is Result.Success) {
                            checkListResults.add(result.data)
                        } else {
                            checkListResults.add(false)
                        }
                    }
                }
            }
        }

        return !checkListResults.contains(false)
    }

    fun setCheck(checkShoppingList: CheckShoppingList, isChecked: Boolean) {

        checkShoppingList.check = isChecked
        Logger.i("checkShoppingList:$checkShoppingList")

        coroutineScope.launch {
            howYoRepository.updateCheckShopList(checkShoppingList)
        }
    }
}