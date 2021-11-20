package com.yuchen.howyo.plan.payment.paymentdetail

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentPaymentDetailBinding
import com.yuchen.howyo.ext.closeKeyBoard
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.plan.payment.PaymentType

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

        val argumentPlan = PaymentDetailFragmentArgs.fromBundle(requireArguments()).plan

        val spinnerList = mutableListOf(argumentPlan.authorId)
        argumentPlan.companionList?.let { spinnerList.addAll(it) }

        binding.spinnerPaymentDetailPayer.adapter = PaymentDetailSpinnerAdapter(
            spinnerList.toList() as List<String>
        )

        binding.radioGroupPaymentDetailType.check(
            when (viewModel.payment.value?.type) {
                PaymentType.SELF.type -> R.id.radio_self_payment
                PaymentType.SHARE.type -> R.id.radio_share_payment
                else -> 0
            }
        )

        viewModel.isSavePayment.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    binding.edittextPaymentDetailItem.closeKeyBoard()
                    binding.edittextPaymentDetailAmount.closeKeyBoard()
                    viewModel.submitPayment()
                    viewModel.onSavePayment()
                }
            }
        }

        viewModel.paymentResult.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    findNavController().popBackStack()
                    viewModel.onSubmitPayment()
                }
            }
        }

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
            R.id.delete -> {
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.payment_confirm_delete))
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            viewModel.deletePayment()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                        }
                        .show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}