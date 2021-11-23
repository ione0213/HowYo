package com.yuchen.howyo.plan.payment

import com.yuchen.howyo.R
import com.yuchen.howyo.util.Util.getString

enum class PaymentType(val type: String) {
    SELF(getString(R.string.payment_type_self_value)),
    SHARE(getString(R.string.payment_type_share_value))
}
