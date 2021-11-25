package com.yuchen.howyo

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
import com.yuchen.howyo.plan.*
import com.yuchen.howyo.plan.checkorshoppinglist.CheckOrShoppingListAdapter
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType
import com.yuchen.howyo.plan.companion.CompanionType
import com.yuchen.howyo.plan.detail.edit.DetailEditImagesAdapter
import com.yuchen.howyo.plan.detail.view.DetailImagesAdapter
import com.yuchen.howyo.plan.findlocation.FindLocationDaysAdapter
import com.yuchen.howyo.profile.PlanAdapter
import com.yuchen.howyo.profile.author.AuthorProfilePlanAdapter
import com.yuchen.howyo.profile.author.FollowType
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.CurrentFragmentType
import com.yuchen.howyo.util.Util.getString

@BindingAdapter("startDate", "endDate")
fun TextView.bindJourneyDate(starDate: Long, endDate: Long) {
    text = HowYoApplication.instance.getString(
        R.string.duration_with_dash, starDate.toFullDate(), endDate.toFullDate()
    )
}

@BindingAdapter("time")
fun TextView.bindTime(time: Long) {
    when {
        time != 0L -> text = time.toTime()
    }
}

@BindingAdapter("schedule")
fun TextView.bindScheduleTimeDuration(schedule: Schedule) {
    val startTime = schedule.startTime
    val endTime = schedule.endTime

    when {
        startTime != null && endTime != null -> {
            if (endTime > startTime) {
                text = HowYoApplication.instance.getString(
                    R.string.schedule_time_duration,
                    (endTime - startTime).toHourString(),
                    (endTime - startTime).toMinuteString()
                )
            }
        }
    }
}

@BindingAdapter("dateTime")
fun TextView.bindTimeWithAgoFormat(time: Long) {
    when {
        time != 0L -> text = time.displayTime()
    }
}

@BindingAdapter("fromTime", "toTime")
fun TextView.bindTimeToTime(fromTime: Long, toTime: Long) {
    when {
        fromTime != 0L || toTime != 0L -> {
            text = HowYoApplication.instance.getString(
                R.string.duration_with_dash, fromTime.toTime(), toTime.toTime()
            )
        }
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
        CurrentFragmentType.SIGNIN -> View.GONE
        else -> View.VISIBLE
    }
}

@BindingAdapter("currentFragmentTypeForToolbar")
fun Toolbar.bindToolbar(currentFragmentType: CurrentFragmentType) {
    visibility = when (currentFragmentType) {
        CurrentFragmentType.PLAN, CurrentFragmentType.SIGNIN -> View.GONE
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
        CurrentFragmentType.AUTHOR_PROFILE,
        CurrentFragmentType.FRIENDS -> sharedFragmentTitle
        else -> currentFragmentTypeForText.value
    }
}

@BindingAdapter("days")
fun RecyclerView.bindRecyclerViewWithDays(days: List<Day>?) {
    days?.let {
        adapter?.apply {
            when (this) {
                is PlanDaysAdapter -> submitDays(it)
                is FindLocationDaysAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("day", "firstDate")
fun TextView.bindDayText(day: Int, firstDate: Long?) {
    val date = firstDate?.plus((1000 * 60 * 60 * 24 * day))

    text = HowYoApplication.instance.getString(R.string.day, day.plus(1), date?.toDate())
}

@BindingAdapter("schedules")
fun bindRecyclerViewWithSchedules(recyclerView: RecyclerView, schedules: List<Schedule>?) {
    schedules?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ScheduleAdapter -> addScheduleOrEmptyPage(it)
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
    photoData: List<PhotoData>?
) {
    val schedulePhotosDisplay = photoData?.filter { it.isDeleted != true }

    schedulePhotosDisplay?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DetailEditImagesAdapter -> addPhotoAndBtn(it)
            }
        }
    }
}

@BindingAdapter("imageUrl")
fun bindPlanAuthorAvatar(imageView: ImageView, imgUrl: String?) {
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
fun bindPlanAuthorAvatar(imageView: ShapeableImageView, imgUrl: String?) {
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

@BindingAdapter("userId", "authorData")
fun bindPlanAuthorAvatar(
    imageView: ShapeableImageView,
    userId: String?,
    authorDataList: Set<User>?
) {
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

@BindingAdapter("userId", "authorData")
fun bindNickName(textView: TextView, userId: String?, authorDataList: Set<User>?) {
    userId?.let {
        val user = authorDataList?.first { it.id == userId }

        textView.text = user?.name
    }
}

@BindingAdapter("imageData")
fun bindImageWithData(imageView: ImageView, photoData: PhotoData?) {
    photoData?.let { photo ->
        when {
            photo.url?.isNotEmpty() == true -> {
                val imgUri = photo.url.toUri().buildUpon().scheme("https").build()

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
                    !photo.uri.toString().contains("drawable") -> {
                        imageView.setImageBitmap(
                            photo.uri?.let { uri ->
                                HowYoApplication.instance
                                    .contentResolver
                                    ?.openFileDescriptor(uri, "r")?.use {
                                        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
                                    }
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
fun AppCompatButton.bindDeleteButtonWithPlanPhoto(planPhotoData: PhotoData?) {
    planPhotoData?.let {
        visibility = when (planPhotoData.url.isNullOrEmpty()) {
            true -> {
                when {
                    planPhotoData.uri.toString() == getString(R.string.default_cover) -> View.GONE
                    else -> View.VISIBLE
                }
            }
            false -> {
                when {
                    planPhotoData.uri.toString() == getString(R.string.default_cover) &&
                            planPhotoData.isDeleted == true -> {
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
                is CheckOrShoppingListAdapter -> addTitleAndItem(it, mainItemType)
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
                is PlanAdapter -> submitList(it)
                is DiscoverAdapter -> submitList(it)
                is AuthorProfilePlanAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("privacy")
fun TextView.bindPlanPrivacyStatus(privacy: String?) {
    text = when (privacy) {
        PlanPrivacy.PRIVATE.value -> getString(R.string.private_plan)
        PlanPrivacy.PUBLIC.value -> getString(R.string.public_plan)
        else -> ""
    }
}

@BindingAdapter("lockButton", "accessType")
fun ImageButton.bindPlanLockButton(privacy: String?, accessType: AccessPlanType?) {
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

@BindingAdapter("unlockButton", "accessType")
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
fun ImageButton.bindHeartButton(plan: Plan?, type: LikeType?, accessType: AccessPlanType?) {
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
fun ImageButton.bindFavoriteButton(plan: Plan?, type: FavoriteType?, accessType: AccessPlanType?) {
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
fun AppCompatButton.bindFollowButton(user: User?, type: FollowType?) {
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
fun AppCompatButton.bindButtonForPlanMembers(plan: Plan?, accessType: AccessPlanType?) {
    val userId = UserManager.userId ?: ""

    visibility =
        when {
            (plan?.authorId == userId || plan?.companionList?.contains(userId) == true) &&
                    accessType == AccessPlanType.VIEW -> {
                View.VISIBLE
            }
            else -> {
                View.GONE
            }
        }
}

@BindingAdapter("companionButton", "user", "companionType")
fun AppCompatButton.bindCompanionButton(plan: Plan?, user: User?, companionType: CompanionType) {
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
fun AppCompatButton.bindGroupPlanIcon(plan: Plan?) {
    if (plan != null) {
        visibility = when (plan.companionList?.contains(UserManager.userId)) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }
}
