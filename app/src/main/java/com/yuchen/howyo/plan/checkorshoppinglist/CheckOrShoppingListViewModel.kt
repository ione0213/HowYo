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
import com.yuchen.howyo.util.Util.getString
import kotlinx.coroutines.*

class CheckOrShoppingListViewModel(
    private val howYoRepository: HowYoRepository,
    private val argumentPlanId: String,
    private val argumentMainType: MainItemType
) : ViewModel() {

    // Handle all check lists
    var allCheckAndShoppingLists = MutableLiveData<List<CheckShoppingList>>()

    val planId: String
        get() = argumentPlanId

    val mainType: MainItemType
        get() = argumentMainType

    val item = MutableLiveData<String>()

    private val _itemCreatedResult = MutableLiveData<Boolean>()

    val itemCreatedResult: LiveData<Boolean>
        get() = _itemCreatedResult

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
        fetchCheckShoppingListResult()
    }

    private fun fetchCheckShoppingListResult() {
        allCheckAndShoppingLists = howYoRepository.getLiveCheckShopList(planId, mainType)
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

            _itemCreatedResult.value = when (result) {
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

                _isChkListDeleted.postValue(deleteAllCheckAndShopList())
                _isChkListReady.postValue(createDefaultCheckList())
            }

            when {
                isChkListDeleted.value == true && isChkListReady.value == true -> {
                    _isAllDataReady.value = true
                }
            }
        }
    }

    private suspend fun deleteAllCheckAndShopList(): Boolean {

        val deleteResult = mutableListOf<Boolean>()

        allCheckAndShoppingLists.value?.forEach {
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
        val itemList = mutableListOf<CheckShoppingList>()

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

                        itemList.add(newItem)
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

                        itemList.add(newItem)
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

                        itemList.add(newItem)
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

                        itemList.add(newItem)
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

                        itemList.add(newItem)
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

                        itemList.add(newItem)
                    }
                }
            }
        }

        checkListResults.add(createDefaultCheckListWithBatch(itemList))

        return !checkListResults.contains(false)
    }

    private suspend fun createDefaultCheckListWithBatch(list: List<CheckShoppingList>): Boolean =
        when (val result = howYoRepository.createCheckShopListWithBatch(list)) {
            is Result.Success -> result.data
            else -> false
        }

    fun setCheck(checkShoppingList: CheckShoppingList, isChecked: Boolean) {

        checkShoppingList.check = isChecked

        coroutineScope.launch {
            howYoRepository.updateCheckShopList(checkShoppingList)
        }
    }
}
