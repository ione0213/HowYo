package com.yuchen.howyo.plan.checkorshoppinglist

import com.yuchen.howyo.R
import com.yuchen.howyo.util.Util.getString

enum class MainItemType(val value: String) {
    CHECK(getString(R.string.check)),
    SHOPPING(getString(R.string.shopping))
}
