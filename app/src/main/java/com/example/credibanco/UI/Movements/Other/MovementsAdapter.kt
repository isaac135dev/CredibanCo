package com.example.credibanco.UI.Movements.Other

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.credibanco.Data.Model.LocalDatabaseAuthorization
import com.example.credibanco.R

class MovementsAdapter(
    private var transactions: List<LocalDatabaseAuthorization>,
    private val onItemClick: (LocalDatabaseAuthorization) -> Unit
) : RecyclerView.Adapter<MovementsListViewHolder>() {

    fun updateData(newTransactions: Any) {
        transactions = newTransactions as List<LocalDatabaseAuthorization>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementsListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movement, parent, false)
        return MovementsListViewHolder(itemView)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: MovementsListViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.receiptIdTextView.text = "Receipt ID: ${transaction.receiptId}"
        holder.rrnTextView.text = "RRN: ${transaction.rrn}"

        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }
    }



}