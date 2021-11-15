package com.yuchen.howyo.plan

import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R

enum class CheckItemType(val list: Array<String>) {
    NECESSARY(HowYoApplication.instance.resources.getStringArray(R.array.necessary_list)),
    CLOTHE(HowYoApplication.instance.resources.getStringArray(R.array.clothe_list)),
    WASH(HowYoApplication.instance.resources.getStringArray(R.array.wash_list)),
    ELECTRONIC(HowYoApplication.instance.resources.getStringArray(R.array.electronic_list)),
    HEALTH(HowYoApplication.instance.resources.getStringArray(R.array.health_list)),
    OTHER(HowYoApplication.instance.resources.getStringArray(R.array.other_list))
}