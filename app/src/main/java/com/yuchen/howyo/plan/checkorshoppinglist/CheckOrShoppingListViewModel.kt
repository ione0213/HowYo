package com.yuchen.howyo.plan.checkorshoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.data.CheckShoppingItem
import com.yuchen.howyo.data.CheckShoppingItemResult
import com.yuchen.howyo.data.source.HowYoRepository

class CheckOrShoppingListViewModel(
    private val howYoRepository: HowYoRepository,
    private val planId: String,
    private val mainType: String
) : ViewModel() {

    //Handle all check lists
    private val _allCheckLists = MutableLiveData<List<CheckShoppingItemResult>>()

    val allCheckLists: LiveData<List<CheckShoppingItemResult>>
        get() = _allCheckLists

    init {
        when (mainType) {
            HowYoApplication.instance.getString(R.string.check_list) -> setMockCheckList()
            HowYoApplication.instance.getString(R.string.shopping_list) -> setMockShoppingList()
        }
    }

    fun setMockCheckList() {
        _allCheckLists.value = listOf(
            CheckShoppingItemResult(
                "必備項目",
                listOf(
                    CheckShoppingItem(
                        "1",
                        "A",
                        item = "護照"
                    ),
                    CheckShoppingItem(
                        "2",
                        "A",
                        item = "手機"
                    ),
                    CheckShoppingItem(
                        "3",
                        "A",
                        item = "錢包"
                    )
                )
            ),
            CheckShoppingItemResult(
                "衣服",
                listOf(
                    CheckShoppingItem(
                        "1",
                        "B",
                        item = "厚外套"
                    ),
                    CheckShoppingItem(
                        "2",
                        "B",
                        item = "內衣"
                    ),
                    CheckShoppingItem(
                        "3",
                        "B",
                        item = "襪子"
                    ),
                    CheckShoppingItem(
                        "4",
                        "B",
                        item = "發熱衣"
                    )
                )
            )
        )
    }

    fun setMockShoppingList() {
        _allCheckLists.value = listOf(
            CheckShoppingItemResult(
                "",
                listOf(
                    CheckShoppingItem(
                        "1",
                        "A",
                        item = "鏡頭"
                    ),
                    CheckShoppingItem(
                        "2",
                        "A",
                        item = "歐咪壓給"
                    ),
                    CheckShoppingItem(
                        "3",
                        "A",
                        item = "贖條三姐妹"
                    )
                )
            )
        )
    }
}