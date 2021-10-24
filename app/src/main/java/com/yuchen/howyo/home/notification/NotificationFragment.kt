package com.yuchen.howyo.home.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentNotificationBinding
import com.yuchen.howyo.ext.getVmFactory

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    val viewModel by viewModels<NotificationViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerNotifications.adapter = NotificationAdapter()

        return binding.root
    }
}