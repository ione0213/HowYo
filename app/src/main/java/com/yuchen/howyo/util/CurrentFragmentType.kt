package com.yuchen.howyo.util

import com.yuchen.howyo.R
import com.yuchen.howyo.util.Util.getString

enum class CurrentFragmentType(val value: String) {
    HOME(getString(R.string.home)),
    DISCOVER(getString(R.string.discover)),
    FAVORITE(getString(R.string.favorite)),
    PLAN(""),
    NOTIFICATION(getString(R.string.notification)),
    GROUP_MESSAGE(getString(R.string.group_message)),
    SHOPPING_LIST(getString(R.string.shopping_list)),
    CHECK_OR_SHOPPING_LIST(getString(R.string.check_list)),
    PAYMENT(getString(R.string.payment)),
    PAYMENT_DETAIL(getString(R.string.payment_detail)),
    FIND_LOCATION(getString(R.string.find_location)),
    PROFILE("User id(tmp)"),
    FRIENDS("User id(tmp)"),
    SETTING(getString(R.string.setting))
}
