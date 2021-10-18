package com.yuchen.howyo.plan.payment.paymentdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuchen.howyo.R
import com.yuchen.howyo.databinding.FragmentPaymentDetailBinding

class PaymentDetailFragment : Fragment() {

    private lateinit var binding: FragmentPaymentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}