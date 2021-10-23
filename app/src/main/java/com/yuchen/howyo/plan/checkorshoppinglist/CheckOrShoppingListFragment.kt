package com.yuchen.howyo.plan.checkorshoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yuchen.howyo.databinding.FragmentCheckOrShoppingListBinding
import com.yuchen.howyo.ext.getVmFactory

class CheckOrShoppingListFragment : Fragment() {

    private lateinit var binding: FragmentCheckOrShoppingListBinding
    private val viewModel by viewModels<CheckOrShoppingListViewModel> {
        getVmFactory(
            CheckOrShoppingListFragmentArgs.fromBundle(requireArguments()).planId,
            CheckOrShoppingListFragmentArgs.fromBundle(requireArguments()).mainType
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCheckOrShoppingListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.recyclerCheckList.adapter = CheckOrShoppingListAdapter(viewModel)

        return binding.root
    }
}