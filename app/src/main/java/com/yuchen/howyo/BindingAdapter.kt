package com.yuchen.howyo

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yuchen.howyo.data.*
import com.yuchen.howyo.ext.toDate
import com.yuchen.howyo.plan.PlanDaysAdapter
import com.yuchen.howyo.plan.ScheduleAdapter
import com.yuchen.howyo.plan.checkorshoppinglist.CheckOrShoppingListAdapter
import com.yuchen.howyo.plan.companion.CompanionAdapter
import com.yuchen.howyo.plan.detail.edit.DetailEditImagesAdapter
import com.yuchen.howyo.plan.detail.view.DetailImagesAdapter
import com.yuchen.howyo.plan.findlocation.FindLocationDaysAdapter
import com.yuchen.howyo.plan.payment.PaymentAdapter
import com.yuchen.howyo.util.CurrentFragmentType
import com.yuchen.howyo.util.Logger

@SuppressLint("SetTextI18n")
@BindingAdapter("startDate", "endDate")
fun TextView.bindJourneyDate(starDate: Long, endDate: Long) {
    text = "${starDate.toDate()} - ${endDate.toDate()}"
}

@BindingAdapter("currentFragmentType")
fun BottomNavigationView.bindBottomView(currentFragmentType: CurrentFragmentType) {
    visibility = when (currentFragmentType) {
        CurrentFragmentType.PLAN,
        CurrentFragmentType.CHECK_OR_SHOPPING_LIST,
        CurrentFragmentType.PAYMENT,
        CurrentFragmentType.PAYMENT_DETAIL -> {
            View.GONE
        }
        else -> View.VISIBLE
    }
}

@BindingAdapter("currentFragmentTypeForText", "sharedFragmentTitle")
fun TextView.bindToolbarTitle(
    currentFragmentTypeForText: CurrentFragmentType,
    sharedFragmentTitle: String
) {
    text = when (currentFragmentTypeForText) {
        CurrentFragmentType.CHECK_OR_SHOPPING_LIST -> {
            sharedFragmentTitle
        }
        else -> currentFragmentTypeForText.value
    }
}

@BindingAdapter("days")
fun bindRecyclerViewWithDays(recyclerView: RecyclerView, days: List<Day>?) {
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

@BindingAdapter("user")
fun bindRecyclerViewWithDays(recyclerView: RecyclerView, user: User) {
    user.let {
        recyclerView.adapter?.apply {
            when (this) {
                is CompanionAdapter -> {
                    submitList(it.followingList)
                }
            }
        }
    }
}

@BindingAdapter("checkLists")
fun bindRecyclerViewWithCheckLists(
    recyclerView: RecyclerView,
    checkLists: List<CheckShoppingItemResult>
) {
    checkLists.let {
        recyclerView.adapter?.apply {
            when (this) {
                is CheckOrShoppingListAdapter -> {
                    addTitleAndItem(it)
                }
            }
        }
    }
}

@BindingAdapter("payments")
fun bindRecyclerViewWithPayments(
    recyclerView: RecyclerView,
    paymentLists: List<Payment>
) {
    paymentLists.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PaymentAdapter -> {
                    submitList(it)
                }
            }
        }
    }
}