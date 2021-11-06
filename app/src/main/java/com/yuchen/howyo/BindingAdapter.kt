package com.yuchen.howyo

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yuchen.howyo.data.*
import com.yuchen.howyo.discover.DiscoverAdapter
import com.yuchen.howyo.ext.toDate
import com.yuchen.howyo.ext.toTime
import com.yuchen.howyo.ext.toWeekDay
import com.yuchen.howyo.favorite.FavoriteAdapter
import com.yuchen.howyo.home.HomeAdapter
import com.yuchen.howyo.home.notification.NotificationAdapter
import com.yuchen.howyo.plan.PlanDaysAdapter
import com.yuchen.howyo.plan.ScheduleAdapter
import com.yuchen.howyo.plan.checkorshoppinglist.CheckOrShoppingListAdapter
import com.yuchen.howyo.plan.companion.CompanionAdapter
import com.yuchen.howyo.plan.detail.edit.DetailEditImagesAdapter
import com.yuchen.howyo.plan.detail.view.DetailImagesAdapter
import com.yuchen.howyo.plan.findlocation.FindLocationDaysAdapter
import com.yuchen.howyo.plan.groupmessage.GroupMessageAdapter
import com.yuchen.howyo.plan.payment.PaymentAdapter
import com.yuchen.howyo.profile.PlanAdapter
import com.yuchen.howyo.profile.friends.item.FriendItemAdapter
import com.yuchen.howyo.util.CurrentFragmentType
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.Util.getString

@SuppressLint("SetTextI18n")
@BindingAdapter("startDate", "endDate")
fun TextView.bindJourneyDate(starDate: Long, endDate: Long) {
    text = "${starDate.toDate()} - ${endDate.toDate()}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("time")
fun TextView.bindMsgTime(time: Long) {

    when {
        time != 0L -> text = time.toTime()
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("fromTime", "toTime")
fun TextView.bindTimeToTime(fromTime: Long, toTime: Long) {

    when {
        fromTime != 0L || toTime != 0L -> text = "${fromTime.toTime()} - ${toTime.toTime()}"
    }
}

@BindingAdapter("currentFragmentType")
fun BottomNavigationView.bindBottomView(currentFragmentType: CurrentFragmentType) {
    visibility = when (currentFragmentType) {
        CurrentFragmentType.PLAN,
        CurrentFragmentType.CHECK_OR_SHOPPING_LIST,
        CurrentFragmentType.PAYMENT,
        CurrentFragmentType.PAYMENT_DETAIL,
        CurrentFragmentType.SETTING,
        CurrentFragmentType.GROUP_MESSAGE -> {
            View.GONE
        }
        else -> View.VISIBLE
    }
}

@BindingAdapter("currentFragmentTypeForToolbar")
fun Toolbar.bindToolbar(currentFragmentType: CurrentFragmentType) {
    visibility = when (currentFragmentType) {
        CurrentFragmentType.PLAN -> {
            View.GONE
        }
        else -> View.VISIBLE
    }
}
//@BindingAdapter("currentFragmentTypeForToolbar")
//fun Toolbar.bindToolbar(currentFragmentType: CurrentFragmentType) {
//    visibility = when (currentFragmentType) {
//        CurrentFragmentType.PLAN,
//        CurrentFragmentType.NOTIFICATION -> {
//            View.GONE
//        }
//        else -> View.VISIBLE
//    }
//}

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
                    submitDays(it)
                }
                is FindLocationDaysAdapter -> {
                    submitList(it)
                }
            }
        }
    }
}

@BindingAdapter("day", "firstDate")
fun TextView.bindTextWithDay(day: Int, firstDate: Long?) {
    val date = firstDate?.plus((1000 * 60 * 60 * 24 * day))
    text = HowYoApplication.instance.getString(R.string.day, day.plus(1), date?.toWeekDay())
}

@BindingAdapter("schedules")
fun bindRecyclerViewWithSchedules(recyclerView: RecyclerView, schedules: List<Schedule>?) {
    Logger.i("Binding schedules: $schedules")
    schedules?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ScheduleAdapter -> addEmptyAndSchedule(it)
            }
        }
    }
}

@BindingAdapter("images")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<String>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DetailImagesAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("photoData")
fun bindRecyclerViewWithPhotoData(
    recyclerView: RecyclerView,
    schedulePhotos: List<SchedulePhoto>?
) {
    val schedulePhotosDisplay = schedulePhotos?.filter { it.isDeleted != true }
    schedulePhotosDisplay?.let {
        recyclerView.adapter?.apply {
            when (this) {
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

@BindingAdapter("imageData")
fun bindImageWithData(imageView: ImageView, schedulePhoto: SchedulePhoto?) {
    Logger.i("schedulePhoto: $schedulePhoto")
    schedulePhoto?.let { photoData ->
        when {
            photoData.url?.isNotEmpty() == true -> {
                val imgUri = photoData.url.toUri().buildUpon().scheme("https").build()
                Glide.with(imageView.context)
                    .load(imgUri)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_placeholder)
                    )
                    .into(imageView)
            }
            else -> {
                when {
                    !photoData.uri.toString().contains("drawable") -> {
                        Logger.i("SELECT")
                        imageView.setImageBitmap(
                            HowYoApplication.instance
                                .contentResolver
                                ?.openFileDescriptor(photoData.uri!!, "r")?.use {
                                    BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
                                }
                        )
                    }
                    else -> {
                        imageView.setImageResource(R.drawable.sample_cover)
                    }
                }
            }
        }
    }
}

@BindingAdapter("planPhoto")
fun AppCompatButton.bindBtnWithPlanPhoto(planPhoto: SchedulePhoto?) {

    planPhoto?.let {
        visibility = when (planPhoto.url.isNullOrEmpty()) {
            true -> {
                when {
                    planPhoto.uri.toString() == getString(R.string.default_cover) -> {
                        View.GONE
                    }
                    else -> View.VISIBLE
                }
            }
            false -> {
                when {
                    planPhoto.uri.toString() == getString(R.string.default_cover) &&
                            planPhoto.isDeleted == true -> {
                        View.GONE
                    }
                    else -> View.VISIBLE
                }
            }
        }
    }
}

@BindingAdapter("scheduleType")
fun bindImageWithScheduleType(imageView: ImageView, scheduleType: String?) {

    scheduleType?.let { type ->
        Logger.i("scheduleType:$scheduleType")
        imageView.setImageResource(
            when (type) {
                getString(R.string.air_flight) -> R.drawable.plane
                getString(R.string.traffic) -> R.drawable.train
                getString(R.string.hotel) -> R.drawable.hotel
                getString(R.string.place) -> R.drawable.place
                else -> 0
            }
        )
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

@BindingAdapter("plans")
fun bindRecyclerViewWithPlans(
    recyclerView: RecyclerView,
    plans: List<Plan>?
) {
    plans?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PlanAdapter -> {
                    submitList(it)
                }
                is HomeAdapter -> {
                    submitList(it)
                }
                is DiscoverAdapter -> {
                    submitList(it)
                }
                is FavoriteAdapter -> {
                    submitList(it)
                }
            }
        }
    }
}

@BindingAdapter("notifications")
fun bindRecyclerViewWithNotifications(
    recyclerView: RecyclerView,
    plans: List<Notification>
) {
    plans.let {
        recyclerView.adapter?.apply {
            when (this) {
                is NotificationAdapter -> {
                    addNotificationItem(it)
                }
            }
        }
    }
}

@BindingAdapter("users")
fun bindRecyclerViewWithUsers(
    recyclerView: RecyclerView,
    plans: List<User>
) {
    plans.let {
        recyclerView.adapter?.apply {
            when (this) {
                is FriendItemAdapter -> {
                    submitList(it)
                }
            }
        }
    }
}

@BindingAdapter("groupMessages")
fun bindRecyclerViewWithMessages(
    recyclerView: RecyclerView,
    groupMessages: List<GroupMessage>
) {
    groupMessages.let {
        recyclerView.adapter?.apply {
            when (this) {
                is GroupMessageAdapter -> {
                    submitList(it)
                }
            }
        }
    }
}