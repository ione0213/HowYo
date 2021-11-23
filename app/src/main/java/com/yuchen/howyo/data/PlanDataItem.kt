package com.yuchen.howyo.data

sealed class PlanDataItem {

    abstract val id: String

    object EmptySchedule : PlanDataItem() {
        override val id = ""
    }

    data class PlanItem(val plan: Plan) : PlanDataItem() {
        override val id: String
            get() = plan.id
    }
}
