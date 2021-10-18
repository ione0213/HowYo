package com.yuchen.howyo.discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentDiscoverBinding

class DiscoverFragment : Fragment() {

    private lateinit var binding: FragmentDiscoverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}