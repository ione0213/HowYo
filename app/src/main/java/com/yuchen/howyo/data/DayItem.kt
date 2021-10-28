package com.yuchen.howyo.data

sealed class DayItem {

    abstract val id: String

    data class FullDayItem(val day: Day) : DayItem() {
        override val id: String
            get() = day.id!!
    }

    object AddBtn : DayItem() {
        override val id: String = ""
    }
}