package com.yuchen.howyo

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yuchen.howyo.data.Day
import com.yuchen.howyo.data.Plan
import com.yuchen.howyo.data.Schedule
import com.yuchen.howyo.ext.toDate
import com.yuchen.howyo.plan.PlanDaysAdapter
import com.yuchen.howyo.plan.ScheduleAdapter
import com.yuchen.howyo.plan.detail.edit.DetailEditImagesAdapter
import com.yuchen.howyo.plan.detail.view.DetailImagesAdapter
import com.yuchen.howyo.plan.findlocation.FindLocationDaysAdapter
import com.yuchen.howyo.util.Logger

@SuppressLint("SetTextI18n")
@BindingAdapter("startDate", "endDate")
fun TextView.bindJourneyDate(starDate: Long, endDate: Long) {
    text = "${starDate.toDate()} - ${endDate.toDate()}"
}

@BindingAdapter("days")
fun bindRecyclerViewWithDays(recyclerView: RecyclerView, days: List<Day>?) {
    Logger.i("days size: ${days?.size}")
    days?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PlanDaysAdapter -> {
                    Logger.i("PlanDaysAdapter")
                    submitList(it)
                }
                is FindLocationDaysAdapter -> {
                    Logger.i("FindLocationDaysAdapter")
                    submitList(it)
                }
            }
        }
    }
}

@BindingAdapter("schedules")
fun bindRecyclerViewWithSchedules(recyclerView: RecyclerView, schedules: List<Schedule>?) {
    schedules?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ScheduleAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("selected")
fun drawDaySelected(button: AppCompatButton, isSelected: Boolean?) {
    button.background = when (isSelected) {
        true -> AppCompatResources.getDrawable(
            HowYoApplication.instance,
            R.drawable.day_corner_selected
        )
        else -> AppCompatResources.getDrawable(
            HowYoApplication.instance,
            R.drawable.day_corner_normal
        )
    }
}

@BindingAdapter("day", "plan")
fun bindDayInfo(button: AppCompatButton, day: Day, plan: Plan) {
//    button.text =
}

@BindingAdapter("images")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<String>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DetailImagesAdapter -> submitList(it)
                is DetailEditImagesAdapter -> addPhotoAndBtn(it)
            }
        }
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
            )
            .into(imageView)
    }
}