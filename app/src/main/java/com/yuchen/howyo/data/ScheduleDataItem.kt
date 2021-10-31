package com.yuchen.howyo.data

sealed class ScheduleDataItem {

    abstract val id: String

    object EmptySchedule : ScheduleDataItem() {
        override val id = ""
    }

    data class ScheduleItem(val schedule: Schedule) : ScheduleDataItem() {
        override val id: String
            get() = schedule.id!!
    }
}