package com.yuchen.howyo.plan.checklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentCheckListBinding

class CheckListFragment : Fragment() {

    private lateinit var binding: FragmentCheckListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCheckListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}