package com.yuchen.howyo.plan

import com.yuchen.howyo.R
import com.yuchen.howyo.util.Util.getString

enum class PlanPrivacy(val value: String) {
    PUBLIC(getString(R.string.plan_public)),
    PRIVATE(getString(R.string.plan_private))
}
