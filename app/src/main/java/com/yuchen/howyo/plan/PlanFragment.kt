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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.R
import com.yuchen.howyo.network.LoadApiStatus
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.launch


class PlanFragment : Fragment() {

    private lateinit var binding: FragmentPlanBinding
    val viewModel by viewModels<PlanViewModel> {
        getVmFactory(
            PlanFragmentArgs.fromBundle(requireArguments()).plan!!,
            PlanFragmentArgs.fromBundle(requireArguments()).accessType
        )
    }

    private val dayItemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter as PlanDaysAdapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                return when {
                    //Prevent moving to the position which is out of days range
                    from != to && to <= viewModel.days.value?.size!!.minus(1) -> {
                        viewModel.moveDay(from, to)
                        adapter.notifyItemMoved(from, to)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    when {
                        !(viewModel.days.value === viewModel.tempDays) -> {
                            lifecycleScope.launch {
                                viewModel.submitMovedDay()
                            }
                        }
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                viewHolder.itemView.alpha = 1.0f
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    private val scheduleItemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter as ScheduleAdapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                Logger.i("From:$from, To:$to")
                Logger.i("viewModel.schedules.value?.size:${viewModel.schedules.value?.size}")
                return when {
                    //Prevent moving to the position which is out of schedules range
                    from != to && to <= viewModel.schedules.value?.size!!.minus(1) -> {
                        viewModel.moveSchedule(from, to)
                        adapter.notifyItemMoved(from, to)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    when {
                        !(viewModel.schedules.value === viewModel.tempSchedules) -> {
                            lifecycleScope.launch {
                                viewModel.submitMoveSchedule()
                            }
                        }
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                viewHolder.itemView.alpha = 1.0f
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        dayItemTouchHelper.attachToRecyclerView(binding.recyclerPlanDays)
        scheduleItemTouchHelper.attachToRecyclerView(binding.recyclerPlanSchedules)
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

        viewModel.selectedDayPosition.observe(viewLifecycleOwner, {
            it?.let {
                Logger.i("selectedDayPosition")
                viewModel.filterSchedule()
            }
        })

        viewModel.deletingPlan.observe(viewLifecycleOwner, {
            it?.let { plan ->
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.confirm_delete_plan, plan.title))
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

        viewModel.deletingDay.observe(viewLifecycleOwner, {
            it?.let { day ->
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.confirm_delete_day, day.position?.plus(1)))
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.delExistDay(day)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            viewModel.onDeletedDay()
                        }
                        .show()
                }
            }
        })

        viewModel.deletingSchedule.observe(viewLifecycleOwner, {
            it?.let { schedule ->
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.confirm_delete_schedule))
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.delExistSchedule(schedule)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            viewModel.onDeletedSchedule()
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
                        viewModel.apply {

                            getPlanResult()
                            getLiveDaysResult()
                            setDefaultSelectedDay()
                            onDeletedDay()
                        }

                        binding.recyclerPlanDays.layoutManager?.scrollToPosition(0)
                    }
                }
            }
        })

        viewModel.allSchedules.observe(viewLifecycleOwner, {
            it?.let {
                Logger.i("allSchedules")
                when (viewModel.deletingPlan.value) {
                    null -> {
                        viewModel.filterSchedule()
                    }
                }
            }
        })

        viewModel.handleScheduleSuccess.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it -> {
                        viewModel.apply {
//                            getLiveSchedulesResult()
                            Logger.i("handleScheduleSuccess")

                            filterSchedule()
                            onDeletedSchedule()
                            setStatusDone()
                        }
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

        viewModel.navigateToEditSchedule.observe(viewLifecycleOwner, {
            it?.let {

                findNavController().navigate(
                    NavigationDirections.navToDetailEditFragment()
                        .setSchedule(null)
                        .setPlanId(viewModel.plan.value?.id)
                        .setDayId(it)
                )
                viewModel.onEditScheduleNavigated()
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

        viewModel.leavePlan.observe(viewLifecycleOwner, {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        return binding.root
    }
}