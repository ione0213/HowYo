package com.yuchen.howyo.home.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.databinding.FragmentNotificationBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.AccessPlanType

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    val viewModel by viewModels<NotificationViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = NotificationAdapter(viewModel)

        binding.recyclerNotifications.adapter = adapter

        viewModel.notifications.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.fetchUserData()
            }
        }

        viewModel.userDataSet.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.notifications.value?.let { notifications ->
                    adapter.addNotificationItem(
                        notifications
                    )
                }
                binding.viewModel = viewModel
            }
        }

        viewModel.sendNotificationResult.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
//                    viewModel.getLiveNotificationsResult()
                    viewModel.fetchUserData()
                    viewModel.onSentNotification()
                }
            }
        }

        viewModel.navigateToUserProfile.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(NavigationDirections.navToAuthorProfileFragment(it))
                viewModel.onUserProfileNavigated()
            }
        }

        viewModel.navigateToPlan.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPlanFragment(
                        it,
                        AccessPlanType.VIEW
                    )
                )

                viewModel.onPlanNavigated()
            }
        }

        return binding.root
    }
}
