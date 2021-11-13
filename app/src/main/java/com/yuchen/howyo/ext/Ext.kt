package com.yuchen.howyo.ext

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View
import com.yuchen.howyo.util.Logger
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
fun Long.toDate(): String {
    return SimpleDateFormat("yyyy/MM/dd").format(this)
}

fun Long.toTime(): String {
    return SimpleDateFormat("HH:mm").format(this)
}

fun Long.toDateTime(): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm a").format(this)
}

fun Long.toWeekDay(): String {
    return SimpleDateFormat("EE").format(this)
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
        sec < 60 -> sec.toString() + "秒前"

        sec < 3600 -> (sec / 60).toString() + "分鐘前"

        sec < 86400 -> (sec / 3600).toString() + "小時前"

        sec < 604800 -> (sec / 86400).toString() + "天前"

        sec < 2592000 -> (sec / 604800).toString() + "周前"

        sec < 31536000 -> (sec / 2592000).toString() + "月前"

        else -> (sec / 31536000).toString() + "年前"
    }
}