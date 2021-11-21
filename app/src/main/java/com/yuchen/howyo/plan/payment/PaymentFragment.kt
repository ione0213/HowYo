package com.yuchen.howyo.plan.payment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.NavigationDirections
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentPaymentBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.detail.edit.DetailEditViewModel
import com.yuchen.howyo.plan.detail.view.DetailFragmentArgs

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private val viewModel by viewModels<PaymentViewModel> {
        getVmFactory(
            PaymentFragmentArgs.fromBundle(requireArguments()).plan
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val adapter = PaymentAdapter(
            PaymentAdapter.OnClickListener {
                viewModel.navigateToEditExistPaymentDetail(it)
            }
        )

        binding.recyclerPaymentItems.adapter = adapter

        binding.viewModel = viewModel

        viewModel.payments.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
                viewModel.calculateShouldPay()
            }
        }

        viewModel.navigateToPaymentDetail.observe(viewLifecycleOwner) {
            it?.let {
                when {
                    it -> {
                        findNavController().navigate(
                            NavigationDirections.navToPaymentDetailFragment(
                                null,
                                viewModel.plan.value!!
                            )
                        )
                        viewModel.onPaymentDetailNavigated()
                    }
                }
            }
        }

        viewModel.navigateToEditExistPaymentDetail.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navToPaymentDetailFragment(
                        it,
                        viewModel.plan.value!!
                    )
                )
                viewModel.onEditExistPaymentDetailNavigated()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)
        menu.findItem(R.id.plus).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.plus -> {
                viewModel.navigateToPaymentDetail()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}