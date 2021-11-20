package com.yuchen.howyo

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageButton
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
import com.google.android.material.imageview.ShapeableImageView
import com.yuchen.howyo.data.*
import com.yuchen.howyo.discover.DiscoverAdapter
import com.yuchen.howyo.ext.*
import com.yuchen.howyo.favorite.FavoriteAdapter
import com.yuchen.howyo.home.HomeAdapter
import com.yuchen.howyo.home.notification.NotificationAdapter
import com.yuchen.howyo.plan.*
import com.yuchen.howyo.plan.checkorshoppinglist.CheckOrShoppingListAdapter
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType
import com.yuchen.howyo.plan.companion.CompanionAdapter
import com.yuchen.howyo.plan.companion.CompanionType
import com.yuchen.howyo.plan.detail.edit.DetailEditImagesAdapter
import com.yuchen.howyo.plan.detail.view.DetailImagesAdapter
import com.yuchen.howyo.plan.findlocation.FindLocationDaysAdapter
import com.yuchen.howyo.plan.groupmessage.GroupMessageAdapter
import com.yuchen.howyo.plan.payment.PaymentAdapter
import com.yuchen.howyo.profile.PlanAdapter
import com.yuchen.howyo.profile.author.AuthorProfilePlanAdapter
import com.yuchen.howyo.profile.author.FollowType
import com.yuchen.howyo.profile.friends.item.FriendItemAdapter
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.CurrentFragmentType
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.Util.getColor
import com.yuchen.howyo.util.Util.getString
import kotlinx.coroutines.withTimeoutOrNull

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

@BindingAdapter("schedule")
fun TextView.bindDurationTime(schedule: Schedule) {

    val startTime = schedule.startTime
    val endTime = schedule.endTime

    when {
        startTime != null && endTime != null -> {
            if (endTime > startTime) {
                text =
                    HowYoApplication.instance.getString(
                        R.string.schedule_time_duration,
                        (endTime - startTime).toHourString(),
                        (endTime - startTime).toMinuteString()
                    )
            }
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("dateTime")
fun TextView.bindCommentTime(time: Long) {

    when {
        time != 0L -> text = time.displayTime()
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
        CurrentFragmentType.COMPANION_LOCATE,
        CurrentFragmentType.PAYMENT,
        CurrentFragmentType.PAYMENT_DETAIL,
        CurrentFragmentType.SETTING,
        CurrentFragmentType.GROUP_MESSAGE,
        CurrentFragmentType.COMMENT,
        CurrentFragmentType.SIGNIN -> {
            View.GONE
        }
        else -> View.VISIBLE
    }
}

@BindingAdapter("currentFragmentTypeForToolbar")
fun Toolbar.bindToolbar(currentFragmentType: CurrentFragmentType) {
    visibility = when (currentFragmentType) {
        CurrentFragmentType.PLAN,
        CurrentFragmentType.SIGNIN -> {
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
        CurrentFragmentType.CHECK_OR_SHOPPING_LIST,
        CurrentFragmentType.PROFILE,
        CurrentFragmentType.AUTHOR_PROFILE -> {
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

@BindingAdapter("imageUrl")
fun bindImage(imageView: ShapeableImageView, imgUrl: String?) {
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

@BindingAdapter("plan", "authorData")
fun bindImage(imageView: ShapeableImageView, plan: Plan?, authorDataList: Set<User>?) {

    Logger.i("Plan:$plan")
    Logger.i("authorDataListBinding:$authorDataList")

    plan?.let {

        val imgUrl = authorDataList?.first { it.id == plan.authorId }?.avatar

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
}

@BindingAdapter("userId", "authorData")
fun bindImage(imageView: ShapeableImageView, userId: String?, authorDataList: Set<User>?) {

    userId?.let {

        val imgUrl = authorDataList?.first { it.id == userId }?.avatar

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
}

@BindingAdapter("imageData")
fun bindImageWithData(imageView: ImageView, schedulePhoto: SchedulePhoto?) {
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
        imageView.setImageResource(
            when (type) {
                getString(R.string.air_flight) -> R.drawable.plane
                getString(R.string.traffic) -> R.drawable.train
                getString(R.string.hotel) -> R.drawable.hotel
                getString(R.string.place) -> R.drawable.place
                getString(R.string.food) -> R.drawable.food
                else -> 0
            }
        )
    }

}

@BindingAdapter("checkLists", "mainType")
fun RecyclerView.bindRecyclerViewWithCheckLists(
    checkLists: List<CheckShoppingList>?,
    mainItemType: MainItemType
) {
    checkLists.let {
        adapter?.apply {
            when (this) {
                is CheckOrShoppingListAdapter -> {
                    addTitleAndItem(it, mainItemType)
                }
            }
        }
    }
}

//@BindingAdapter("payments")
//fun bindRecyclerViewWithPayments(
//    recyclerView: RecyclerView,
//    paymentLists: List<Payment>
//) {
//    paymentLists.let {
//        recyclerView.adapter?.apply {
//            when (this) {
//                is PaymentAdapter -> {
//                    submitList(it)
//                }
//            }
//        }
//    }
//}

@BindingAdapter("plans")
fun bindRecyclerViewWithPlans(
    recyclerView: RecyclerView,
    plans: List<Plan>?
) {
    Logger.i("BindingAdapter: $plans")
    plans?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PlanAdapter -> {
                    submitList(it)
                }
                is DiscoverAdapter -> {
                    submitList(it)
                }
                is AuthorProfilePlanAdapter -> {
                    submitList(it)
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

//@BindingAdapter("groupMessages")
//fun bindRecyclerViewWithMessages(
//    recyclerView: RecyclerView,
//    groupMessages: List<GroupMessage>
//) {
//    groupMessages.let {
//        recyclerView.adapter?.apply {
//            when (this) {
//                is GroupMessageAdapter -> {
//                    submitList(it)
//                }
//            }
//        }
//    }
//}

@BindingAdapter("privacy")
fun TextView.bindPrivacyStatus(privacy: String?) {
    text = when (privacy) {
        PlanPrivacy.PRIVATE.value -> getString(R.string.private_plan)
        PlanPrivacy.PUBLIC.value -> getString(R.string.public_plan)
        else -> ""
    }
}

@BindingAdapter("lockBtn", "accessType")
fun ImageButton.bindLockBtn(privacy: String?, accessType: AccessPlanType?) {

    visibility = when (accessType) {
        AccessPlanType.EDIT -> {
            when (privacy) {
                PlanPrivacy.PRIVATE.value -> View.VISIBLE
                PlanPrivacy.PUBLIC.value -> View.GONE
                else -> View.GONE
            }
        }
        else -> View.GONE
    }
}

@BindingAdapter("unlockBtn", "accessType")
fun ImageButton.bindUnlockBtn(privacy: String?, accessType: AccessPlanType?) {
    visibility = when (accessType) {
        AccessPlanType.EDIT -> {
            when (privacy) {
                PlanPrivacy.PRIVATE.value -> View.GONE
                PlanPrivacy.PUBLIC.value -> View.VISIBLE
                else -> View.GONE
            }
        }
        else -> View.GONE
    }
}

@BindingAdapter("heartButton", "likeType", "accessType")
fun ImageButton.bindHeartBtn(plan: Plan?, type: LikeType?, accessType: AccessPlanType?) {
    visibility = when (accessType) {
        AccessPlanType.VIEW -> {
            when (type) {
                LikeType.LIKE -> {
                    when {
                        plan?.likeList?.contains(UserManager.userId) == true -> View.VISIBLE
                        else -> View.GONE
                    }
                }
                LikeType.UNLIKE -> {
                    when {
                        plan?.likeList?.contains(UserManager.userId) == true -> View.GONE
                        else -> View.VISIBLE
                    }
                }
                else -> View.GONE
            }
        }
        else -> View.GONE
    }
}

@BindingAdapter("favoriteButton", "favoriteType", "accessType")
fun ImageButton.bindFavoriteBtn(plan: Plan?, type: FavoriteType?, accessType: AccessPlanType?) {
    visibility = when (accessType) {
        AccessPlanType.VIEW -> {

            when (type) {
                FavoriteType.COLLECT -> {
                    when {
                        plan?.planCollectedList?.contains(UserManager.userId) == true -> View.VISIBLE
                        else -> View.GONE
                    }
                }
                FavoriteType.REMOVE -> {
                    when {
                        plan?.planCollectedList?.contains(UserManager.userId) == true -> View.GONE
                        else -> View.VISIBLE
                    }
                }
                else -> View.GONE
            }
        }
        else -> View.GONE
    }
}

@BindingAdapter("followButton", "followType")
fun AppCompatButton.bindFollowBtn(user: User?, type: FollowType?) {
    visibility = when (user?.id) {
        UserManager.userId -> {
            View.GONE
        }
        else -> {
            when (type) {
                FollowType.FOLLOW -> {
                    when {
                        user?.fansList?.contains(UserManager.userId) == true -> View.VISIBLE
                        else -> View.GONE
                    }
                }
                FollowType.UNFOLLOW -> {
                    when {
                        user?.fansList?.contains(UserManager.userId) == true -> View.GONE
                        else -> View.VISIBLE
                    }
                }
                else -> View.GONE
            }
        }
    }
}

@BindingAdapter("buttonForAuthor", "accessType")
fun AppCompatButton.bindBtnForAuthor(plan: Plan?, accessType: AccessPlanType?) {

    val userId = UserManager.userId ?: ""

    visibility =
        when {
            (plan?.authorId == userId || plan?.companionList?.contains(userId) == true)
                    && accessType == AccessPlanType.VIEW -> {
                View.VISIBLE
            }
            else -> {
                View.GONE
            }
        }
}

@BindingAdapter("companionBtn", "user", "companionType")
fun AppCompatButton.bindCompanionBtn(plan: Plan?, user: User?, companionType: CompanionType) {

    when (companionType) {
        CompanionType.ADD -> {
            visibility = when (plan?.companionList?.contains(user?.id)) {
                true -> View.GONE
                false -> View.VISIBLE
                else -> View.GONE
            }
        }
        CompanionType.REMOVE -> {
            visibility = when (plan?.companionList?.contains(user?.id)) {
                true -> View.VISIBLE
                false -> View.GONE
                else -> View.GONE
            }
        }
    }
}

@BindingAdapter("groupPlan")
fun AppCompatButton.bindGroupText(plan: Plan?) {

    if (plan != null) {
        visibility = when (plan.companionList?.contains(UserManager.userId)) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }
}