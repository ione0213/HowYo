package com.yuchen.howyo.profile.friends

import com.yuchen.howyo.R
import com.yuchen.howyo.util.Util.getString

enum class FriendFilter(val value: String) {
    FANS(getString(R.string.fans_tab)),
    FOLLOWING(getString(R.string.following_tab))
}
