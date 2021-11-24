package com.yuchen.howyo.plan.checkorshoppinglist

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yuchen.howyo.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCheckOrShoppingListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = CheckOrShoppingListAdapter(viewModel)
        binding.recyclerCheckList.adapter = adapter

        viewModel.itemCreatedResult.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.onCreatedItem()
                    adapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.isResetItem.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    context?.let { context ->
                        AlertDialog.Builder(context)
                            .setMessage(getString(R.string.confirm_reset_check_list))
                            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                                viewModel.resetCheckList()
                            }
                            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                                viewModel.onResetItem()
                            }
                            .show()
                    }
                }
            }
        }

        viewModel.isAllDataReady.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.onResetItem()
                }
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        when (viewModel.mainType) {

            MainItemType.CHECK -> {
                inflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)
                menu.findItem(R.id.resetCheckItem).isVisible = true
            }
            else -> {
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.resetCheckItem -> {
                viewModel.resetItem()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
