package com.yuchen.howyo.plan.findlocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentFindLocationBinding

class FindLocationFragment : Fragment() {

    private lateinit var binding: FragmentFindLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFindLocationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}