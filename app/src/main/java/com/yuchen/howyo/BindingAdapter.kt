package com.yuchen.howyo

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
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

@SuppressLint("SetTextI18n")
@BindingAdapter("startDate", "endDate")
fun TextView.bindJourneyDate(starDate: Long, endDate: Long) {
    Logger.i("start date: $starDate, end date: $endDate")
    text = "${starDate.toDate()} - ${endDate.toDate()}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("time")
fun TextView.bindMsgTime(time: Long) {
    text = time.toTime()
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

@BindingAdapter("day", "firstDate")
fun TextView.bindTextWithDay(day: Int, firstDate: Long?) {
    val date = firstDate?.plus((1000 * 60 * 60 * 24 * day))
    text = HowYoApplication.instance.getString(R.string.day, day.plus(1), date?.toWeekDay())
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

//@BindingAdapter("selected")
//fun drawDaySelected(textView: TextView, isSelected: Boolean?) {
//    textView.background = when (isSelected) {
//        true -> AppCompatResources.getDrawable(
//            HowYoApplication.instance,
//            R.drawable.day_corner_selected
//        )
//        else -> AppCompatResources.getDrawable(
//            HowYoApplication.instance,
//            R.drawable.day_corner_normal
//        )
//    }
//}

//@BindingAdapter("day", "plan")
//fun bindDayInfo(button: AppCompatButton, day: Day, plan: Plan) {
////    button.text =
//}

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

@BindingAdapter("plans")
fun bindRecyclerViewWithPlans(
    recyclerView: RecyclerView,
    plans: List<Plan>
) {
    plans.let {
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