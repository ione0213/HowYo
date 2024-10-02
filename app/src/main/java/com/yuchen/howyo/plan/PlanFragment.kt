package com.yuchen.howyo.plan

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentPlanBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.checkorshoppinglist.MainItemType
import com.yuchen.howyo.util.Util.getColor
import kotlinx.coroutines.launch

class PlanFragment : Fragment() {
    private lateinit var binding: FragmentPlanBinding
    val viewModel by viewModels<PlanViewModel> {
        getVmFactory(
            PlanFragmentArgs.fromBundle(requireArguments()).plan,
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
                    // Prevent moving to the position which is out of days range
                    from != to && to <= viewModel.days.value?.size?.minus(1) ?: 1 -> {
                        viewModel.moveDay(from, to)
                        adapter.notifyItemMoved(from, to)
                        true
                    }
                    else -> false
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
                        !(viewModel.days.value === viewModel.movedDays) -> {
                            lifecycleScope.launch {
                                viewModel.submitMovedDay()
                                viewModel.submitMoveSchedule(HandleScheduleType.TIME)
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

                return when {
                    // Prevent moving to the position which is out of schedules range
                    from != to && to <= viewModel.schedules.value?.size?.minus(1) ?: 1 -> {
                        viewModel.moveSchedule(from, to)
                        adapter.notifyItemMoved(from, to)
                        true
                    }
                    else -> false
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
                        !(viewModel.schedules.value === viewModel.movedSchedules) -> {
                            lifecycleScope.launch {
                                viewModel.submitMoveSchedule(HandleScheduleType.POSITION)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        when (viewModel.accessType) {
            AccessPlanType.EDIT -> {
                dayItemTouchHelper.attachToRecyclerView(binding.recyclerPlanDays)
                scheduleItemTouchHelper.attachToRecyclerView(binding.recyclerPlanSchedules)
            }
            else -> {

            }
        }

        binding.recyclerPlanDays.adapter = PlanDaysAdapter(viewModel)
        binding.recyclerPlanSchedules.adapter =
            ScheduleAdapter(
                viewModel,
                ScheduleAdapter.OnClickListener {
                    viewModel.navigateToDetail(it)
                }
            )

        // Reduce saturation to make text clear
        val cm = ColorMatrix()
        cm.setSaturation(0.4F)
        val grayColorFilter = ColorMatrixColorFilter(cm)
        binding.imgPlanCover.colorFilter = grayColorFilter

        setToolbar()

        var isShow = true
        var scrollRange = -1
        binding.appBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
                if (scrollRange == -1) {
                    scrollRange = barLayout?.totalScrollRange ?: 0
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbar.setCollapsedTitleTextColor(getColor(R.color.matcha_6))
                    binding.collapsingToolbar.title = viewModel.plan.value?.title
                    isShow = true
                } else if (isShow) {
                    binding.collapsingToolbar.title = " "
                    isShow = false
                }
            }
        )

        viewModel.selectedDayPosition.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.filterSchedule()
            }
        }

        viewModel.deletingPlan.observe(viewLifecycleOwner) {
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
        }

        viewModel.deletingDay.observe(viewLifecycleOwner) {
            it?.let { day ->
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.confirm_delete_day, day.position.plus(1)))
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.delExistDay(day)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            viewModel.onDeletedDay()
                        }
                        .show()
                }
            }
        }

        viewModel.deletingSchedule.observe(viewLifecycleOwner) {
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
        }

        viewModel.updatePrivacy.observe(viewLifecycleOwner) {
            it?.let {
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(
                            getString(
                                R.string.confirm_update_privacy,
                                when (it) {
                                    PlanPrivacy.PRIVATE -> getString(R.string.set_private)
                                    PlanPrivacy.PUBLIC -> getString(R.string.set_public)
                                }
                            )
                        )
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.setPrivacy(it)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            viewModel.onUpdatedPrivacy()
                        }
                        .show()
                }
            }
        }

        viewModel.updatePrivacyResult.observe(viewLifecycleOwner) {
            it?.let {
                if (it) viewModel.onUpdatedPrivacy()
            }
        }

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.navigateToHomeAfterDeletingPlan.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    mainViewModel.navigateToHomeByBottomNav()
                    resetToolbar()
                    viewModel.onNavigatedHome()
                }
            }
        }

        viewModel.handleDayResult.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.apply {
                        setDefaultSelectedDay()
                        onDeletedDay()
                        onSubmitMoveDay()
                        setStatusDone()
                    }

                    binding.recyclerPlanDays.layoutManager?.scrollToPosition(0)
                }
            }
        }

        viewModel.allSchedules.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.deletingPlan.value) {
                    null -> viewModel.filterSchedule()
                }
            }
        }

        viewModel.handleScheduleResult.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.apply {
                        filterSchedule()
                        onDeletedSchedule()
                        setStatusDone()
                        onSubmitMoveSchedule()
                    }
                }
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.accessType) {
                    AccessPlanType.VIEW -> {
                        findNavController().navigate(
                            NavigationDirections.navToDetailFragment(
                                it,
                                viewModel.plan.value,
                                viewModel.selectedDayPosition.value?.let { position ->
                                    viewModel.days.value?.get(position)
                                }
                            )
                        )
                    }
                    AccessPlanType.EDIT -> {
                        findNavController().navigate(
                            NavigationDirections.navToDetailEditFragment()
                                .setPlan(viewModel.plan.value)
                                .setDay(
                                    viewModel.selectedDayPosition.value?.let { position ->
                                        viewModel.days.value?.get(position)
                                    }
                                )
                                .setSchedule(it)
                        )
                    }
                }

                viewModel.onDetailNavigated()
            }
        }

        viewModel.navigateToAddSchedule.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToDetailEditFragment()
                        .setSchedule(null)
                        .setPlan(viewModel.plan.value)
                        .setDay(it)
                )

                viewModel.onAddScheduleNavigated()
            }
        }

        viewModel.navigateToEditPlan.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPlanFragment(
                        it,
                        AccessPlanType.EDIT
                    )
                )

                viewModel.onEditPlanNavigated()
            }
        }

        viewModel.navigateToEditCover.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPlanCoverDialog().setPlan(it)
                )

                viewModel.onEditCoverNavigated()
            }
        }

        viewModel.navigateToCopyPlan.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToCopyPlanDialog(it)
                )

                viewModel.onCopyPlanNavigated()
            }
        }

        viewModel.navigateToMapMode.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(NavigationDirections.navToFindLocationFragment())
                viewModel.onMapModeNavigated()
            }
        }

        viewModel.navigateToCompanion.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    findNavController().navigate(
                        NavigationDirections.navToCompanionFragment(viewModel.plan.value)
                    )

                    resetToolbar()

                    viewModel.onCompanionNavigated()
                }
            }
        }

        viewModel.navigateToGroupMessage.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToGroupMessageFragment(it)
                )

                resetToolbar()

                viewModel.onGroupMessageNavigated()
            }
        }

        viewModel.navigateToLocateCompanion.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToLocateCompanionFragment(it)
                )

                resetToolbar()

                viewModel.onLocateCompanionNavigated()
            }
        }

        viewModel.navigateToComment.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToComment(it)
                )

                resetToolbar()

                viewModel.onCommentNavigated()
            }
        }

        viewModel.navigateToCheckOrShoppingList.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToCheckListFragment(
                        viewModel.plan.value?.id ?: "",
                        it
                    )
                )

                viewModel.onCheckLIstNavigated()

                resetToolbar()

                mainViewModel.setSharedToolbarTitle(
                    when (it) {
                        MainItemType.CHECK -> getString(R.string.check_list)
                        MainItemType.SHOPPING -> getString(R.string.shopping_list)
                    }
                )
            }
        }

        viewModel.navigateToPayment.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPaymentFragment(it)
                )

                mainViewModel.resetToolbar()

                viewModel.onPaymentNavigated()
            }
        }

        viewModel.leavePlan.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        }

        viewModel.navigateToAuthorProfile.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(NavigationDirections.navToAuthorProfileFragment(it))
                mainViewModel.resetToolbar()
                viewModel.onAuthorProfileNavigated()
            }
        }

        return binding.root
    }

    private fun setToolbar() {
        (activity as AppCompatActivity?)?.setSupportActionBar(binding.planToolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)

        menu.findItem(R.id.delete).apply {
            if (viewModel.accessType == AccessPlanType.EDIT) isVisible = true
        }

        // For collapsed title align
        menu.findItem(R.id.nothing).apply {
            if (viewModel.accessType == AccessPlanType.VIEW) isVisible = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                icon?.colorFilter = BlendModeColorFilter(
                    Color.TRANSPARENT, BlendMode.SRC_IN
                )
            } else {
                icon?.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN)
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete -> viewModel.checkDeletePlan()
            android.R.id.home -> {
                resetToolbar()

                activity?.invalidateOptionsMenu()

                viewModel.leavePlan()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun resetToolbar() {
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mainViewModel.resetToolbar()
    }
}
