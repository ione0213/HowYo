package com.yuchen.howyo.plan.payment.paymentdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentPaymentDetailBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.detail.edit.DetailEditSpinnerAdapter
import com.yuchen.howyo.plan.payment.PaymentFragmentArgs
import com.yuchen.howyo.plan.payment.PaymentViewModel

class PaymentDetailFragment : Fragment() {

    private lateinit var binding: FragmentPaymentDetailBinding
    private val viewModel by viewModels<PaymentDetailViewModel> {
        getVmFactory(
            PaymentDetailFragmentArgs.fromBundle(requireArguments()).payment,
            PaymentDetailFragmentArgs.fromBundle(requireArguments()).plan
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

        binding = FragmentPaymentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.spinnerPaymentDetailPayer.adapter = PaymentDetailSpinnerAdapter(
            viewModel.plan.value?.companionList ?: listOf()
        )

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)
        when {
            PaymentDetailFragmentArgs.fromBundle(requireArguments()).payment != null -> {
                menu.findItem(R.id.delete).isVisible = true
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.plus -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}