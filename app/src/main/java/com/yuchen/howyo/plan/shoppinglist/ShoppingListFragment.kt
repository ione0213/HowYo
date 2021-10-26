package com.yuchen.howyo.plan.shoppinglist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentShoppingListBinding

class ShoppingListFragment : Fragment() {

    private lateinit var binding: FragmentShoppingListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}