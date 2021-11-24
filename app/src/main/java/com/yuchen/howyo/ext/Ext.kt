package com.yuchen.howyo.ext

import android.graphics.Rect
import android.location.Location
import android.view.TouchDelegate
import android.view.View
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Increase touch area of the view/button .
 */
fun View.setTouchDelegate() {
    val parent = this.parent as View // button: the view you want to enlarge hit area
    parent.post {
        val rect = Rect()
        this.getHitRect(rect)
        rect.top -= 100 // increase top hit area
        rect.left -= 100 // increase left hit area
        rect.bottom += 100 // increase bottom hit area
        rect.right += 100 // increase right hit area
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}

/**
 * Transfer data form long to date string.
 */
fun Long.toFullDate(): String {
    return SimpleDateFormat("yyyy/MM/dd").format(this)
}

fun Long.toTime(): String {
    return SimpleDateFormat("HH:mm").format(this)
}

fun Long.toDateTime(): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm a").format(this)
}

fun Long.toDate(): String {
    return SimpleDateFormat("MM/dd").format(this)
}

fun Long.toHour(): Int {
    return SimpleDateFormat("HH").format(this).toInt()
}

fun Long.toHourString(): String = "${(this / 1000 / 60 / 60)}"

fun Long.toMinuteString(): String = "${(this / 1000 / 60 % 60)}"

fun Long.toMinute(): Int {
    return SimpleDateFormat("mm").format(this).toInt()
}

fun Long.displayTime(): String {
    val sec = (Calendar.getInstance().timeInMillis - this) / 1000
    return when {
        sec < 60 -> HowYoApplication.instance.getString(R.string.seconds_ago, sec.toString())

        sec < 3600 -> HowYoApplication.instance.getString(R.string.minutes_ago, (sec / 60).toString())

        sec < 86400 -> HowYoApplication.instance.getString(R.string.hours_ago, (sec / 3660).toString())

        sec < 604800 -> HowYoApplication.instance.getString(R.string.days_ago, (sec / 86400).toString())

        sec < 2592000 -> HowYoApplication.instance.getString(R.string.weeks_ago, (sec / 604800).toString())

        sec < 31536000 -> HowYoApplication.instance.getString(R.string.months_ago, (sec / 2592000).toString())

        else -> HowYoApplication.instance.getString(R.string.years_ago, (sec / 31536000).toString())
    }
}

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}
