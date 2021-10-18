package com.yuchen.howyo.profile.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}