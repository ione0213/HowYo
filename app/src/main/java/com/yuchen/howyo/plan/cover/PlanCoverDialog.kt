package com.yuchen.howyo.plan.cover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.yuchen.howyo.databinding.DialogPlanCoverBinding

class PlanCoverDialog : AppCompatDialogFragment() {

    private lateinit var binding: DialogPlanCoverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogPlanCoverBinding.inflate(inflater,  container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}