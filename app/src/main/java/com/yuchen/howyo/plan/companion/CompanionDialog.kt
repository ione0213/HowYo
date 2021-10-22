package com.yuchen.howyo.plan.companion

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.DialogCompanionBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.ext.setTouchDelegate

class CompanionDialog : AppCompatDialogFragment() {

    private lateinit var binding: DialogCompanionBinding
    private val viewModel by viewModels<CompanionViewModel> {
        getVmFactory(
            CompanionDialogArgs.fromBundle(requireArguments()).user
//            CompanionDialogArgs.fromBundle(requireArguments()).plan
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogCompanionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerCompanionFriends.adapter = CompanionAdapter(viewModel)
        binding.btnCompanionClose.setTouchDelegate()

        viewModel.leave.observe(viewLifecycleOwner, {

            it?.let {
                this.dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        return binding.root
    }

    override fun dismiss() {
        binding.layoutCompanion.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_slide_down
            )
        )
        Handler().postDelayed({ super.dismiss() }, 200)
    }
}