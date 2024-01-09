package com.example.credibanco.UI.Movements.Other

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.credibanco.R

class MovementsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val receiptIdTextView: TextView = itemView.findViewById(R.id.textViewReceiptId)
    val rrnTextView: TextView = itemView.findViewById(R.id.textViewRRN)
}