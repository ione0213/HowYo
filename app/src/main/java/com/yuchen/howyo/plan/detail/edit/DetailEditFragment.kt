package com.yuchen.howyo.plan.detail.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentDetailEditBinding

class DetailEditFragment : Fragment() {

    private lateinit var binding: FragmentDetailEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}