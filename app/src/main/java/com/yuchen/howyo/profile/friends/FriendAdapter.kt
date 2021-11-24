package com.yuchen.howyo.profile.friends

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.yuchen.howyo.profile.friends.item.FriendItemFragment

class FriendAdapter(fragmentManager: FragmentManager, userId: String) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val currentUserId = userId

    override fun getItem(position: Int): Fragment {
        return FriendItemFragment(FriendFilter.values()[position], currentUserId)
    }

    override fun getCount() = FriendFilter.values().size

    override fun getPageTitle(position: Int): CharSequence {
        return FriendFilter.values()[position].value
    }
}
