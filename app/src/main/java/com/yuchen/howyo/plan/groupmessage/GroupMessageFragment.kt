package com.yuchen.howyo.plan.groupmessage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentGroupMessageBinding

class GroupMessageFragment : Fragment() {

    private lateinit var binding: FragmentGroupMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupMessageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}