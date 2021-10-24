package com.yuchen.howyo.plan.cover

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.DialogPlanCoverBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.ext.setTouchDelegate
import com.yuchen.howyo.plan.AccessPlanType
import com.yuchen.howyo.util.Logger
import java.util.*

const val TAG = "DATE_PICKER"
class PlanCoverDialog : AppCompatDialogFragment() {

    private lateinit var binding: DialogPlanCoverBinding
    private val viewModel by viewModels<PlanCoverViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogPlanCoverBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.btnPlanCoverClose.setTouchDelegate()

        viewModel.leave.observe(viewLifecycleOwner, {
            it?.let {
                this.dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        viewModel.selectDate.observe(viewLifecycleOwner, {
            showDateRangePicker()
        })

        viewModel.save.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(NavigationDirections.navToPlanFragment(
                    it,
                    AccessPlanType.EDIT
                ))
                viewModel.onSaveCompleted()
            }
        })

        return binding.root
    }

    override fun dismiss() {
        binding.layoutPlanCover.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_slide_down
            )
        )
        Handler().postDelayed({ super.dismiss() }, 200)
    }

    private fun showDateRangePicker() {

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.select_plan_duration))
                .build()

        dateRangePicker.show(childFragmentManager, TAG)

        dateRangePicker.addOnPositiveButtonClickListener {
            Logger.i("${it.first}, ${it.second}")
            viewModel.apply {
                startDate.value = it.first
                endDate.value = it.second
            }
        }
    }
}