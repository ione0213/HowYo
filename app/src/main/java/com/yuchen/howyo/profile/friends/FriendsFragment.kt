package com.yuchen.howyo.profile.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.yuchen.howyo.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tabTypePosition =
            when (FriendsFragmentArgs.fromBundle(requireArguments()).tabType) {
                FriendFilter.FANS -> 0
                FriendFilter.FOLLOWING -> 1
            }
        val userId = FriendsFragmentArgs.fromBundle(requireArguments()).userId

        FragmentFriendsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner

            viewpagerFriend.let {
                tabsFriend.setupWithViewPager(it)
                it.adapter = FriendAdapter(
                    childFragmentManager, userId
                )
                it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabsFriend))
            }

            tabsFriend.setScrollPosition(tabTypePosition, 0f, true)

            viewpagerFriend.currentItem = tabTypePosition

            return@onCreateView root
        }
    }
}
