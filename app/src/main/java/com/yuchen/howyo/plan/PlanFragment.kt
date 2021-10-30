package com.yuchen.howyo.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.databinding.FragmentPlanBinding
import com.yuchen.howyo.ext.getVmFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.appcompat.app.AlertDialog
import com.yuchen.howyo.R
import com.yuchen.howyo.util.Logger


class PlanFragment : Fragment() {

    private lateinit var binding: FragmentPlanBinding
    val viewModel by viewModels<PlanViewModel> {
        getVmFactory(
            PlanFragmentArgs.fromBundle(requireArguments()).plan!!,
            PlanFragmentArgs.fromBundle(requireArguments()).accessType
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerPlanDays.adapter = PlanDaysAdapter(viewModel)
        binding.recyclerPlanSchedules.adapter =
            ScheduleAdapter(viewModel, ScheduleAdapter.OnClickListener {
                viewModel.navigateToDetail(it)
            })

        //Reduce saturation to make text clear
        val cm = ColorMatrix()
        cm.setSaturation(0.4F)
        val grayColorFilter = ColorMatrixColorFilter(cm)
        binding.imgPlanCover.colorFilter = grayColorFilter

        viewModel.deletingDay.observe(viewLifecycleOwner, {
            it?.let { day ->
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.confirm_delete_day, day.position?.plus(1)))
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.delExistDay(day)
                        }
                        .setNegativeButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.onDeletedDay()
                        }
                        .show()
                }
            }
        })

        viewModel.deletingPlan.observe(viewLifecycleOwner, {
            it?.let { plan ->
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.confirm_delete_plan))
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.delExistPlan(plan)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            viewModel.onDeletedPlan()
                        }
                        .show()
                }
            }
        })

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.navigateToHomeAfterDeletingPlan.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        Logger.i("DOOOOOOOOOOOOOONNNNE")
                        mainViewModel.navigateToHomeByBottomNav()
                        viewModel.onNavigatedHome()
                    }
                }
            }
        })

        viewModel.handleDaySuccess.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        viewModel.getPlanResult()
                        viewModel.getLiveDaysResult()
                    }
                }
            }
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(NavigationDirections.navToDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        viewModel.navigateToMapMode.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(NavigationDirections.navToFindLocationFragment())
                viewModel.onMapModeNavigated()
            }
        })

        viewModel.navigateToCompanion.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToCompanionDialog(
                        it
//                        viewModel.plan.value ?: Plan()
                    )
                )
                viewModel.onCompanionNavigated()
            }
        })

        viewModel.navigateToGroupMessage.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToGroupMessageFragment(it)
                )
                viewModel.onGroupMessageNavigated()
            }
        })

        viewModel.navigateToLocateCompanion.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToLocateCompanionFragment(it)
                )
                viewModel.onLocateCompanionNavigated()
            }
        })

        viewModel.navigateToCheckOrShoppingList.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToCheckListFragment(
                        viewModel.plan.value?.id ?: "",
                        it
                    )
                )
                viewModel.onCheckLIstNavigated()
                mainViewModel.setSharedToolbarTitle(it)
            }
        })

        viewModel.navigateToPayment.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPaymentFragment(it)
                )
                viewModel.onPaymentNavigated()
            }
        })

        return binding.root
    }
}