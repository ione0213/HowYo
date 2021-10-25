package com.yuchen.howyo.plan.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.howyo.data.Payment
import com.yuchen.howyo.databinding.ItemPaymentBinding

class PaymentAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Payment, PaymentAdapter.PaymentViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (payment: Payment) -> Unit) {
        fun onClick(payment: Payment) = clickListener(payment)
    }

    class PaymentViewHolder(private var binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(payment: Payment, onClickListener: OnClickListener) {

            binding.payment = payment
            binding.root.setOnClickListener { onClickListener.onClick(payment) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder(
            ItemPaymentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }
}