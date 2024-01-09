package com.example.credibanco.UI.Movements.View

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.credibanco.Data.DataBase.TransactionDatabase
import com.example.credibanco.Data.Model.LocalDatabaseAuthorization
import com.example.credibanco.UI.Movements.Other.MovementsAdapter
import com.example.credibanco.UI.Movements.View.ItemViewDetailActivity.Companion.EXTRA_ID
import com.example.credibanco.UI.Movements.View.ItemViewDetailActivity.Companion.EXTRA_IDSecond
import com.example.credibanco.UI.Movements.View.ItemViewDetailActivity.Companion.EXTRA_Status
import com.example.credibanco.databinding.FragmentMovementsViewBinding

class MovementsView : Fragment() {


    private var binding: FragmentMovementsViewBinding? = null
    private lateinit var adapter: MovementsAdapter
    private var allTransactions: List<LocalDatabaseAuthorization> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMovementsViewBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = TransactionDatabase(requireContext())
        val transactions = dbHelper.getAllTransactions()
        allTransactions = getAllApprovedTransactions()
        adapter = MovementsAdapter(allTransactions) { selectedTransaction ->
            showAndCancelTransactionDetail(selectedTransaction)
        }
        allTransactions = transactions

        binding?.recyclerViewMovements?.adapter = adapter
        binding?.recyclerViewMovements?.layoutManager = LinearLayoutManager(requireContext())
        binding?.editTextSearch?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchQuery = binding?.editTextSearch?.text.toString()
                searchTransaction(searchQuery)
                true
            } else {
                false
            }
        }
    }

    private fun getAllApprovedTransactions(): List<LocalDatabaseAuthorization> {
        val dbHelper = TransactionDatabase(requireContext())
        return dbHelper.getAllTransactions().filter { it.statusDescription == "Aprobada" }
    }

    private fun getAllDeniedTransactions(): List<LocalDatabaseAuthorization> {
        val dbHelper = TransactionDatabase(requireContext())
        return dbHelper.getAllTransactions().filter { it.statusDescription == "Denegada" }
    }

    private fun showAndCancelTransactionDetail(selectedTransaction: LocalDatabaseAuthorization?) {
        if (selectedTransaction != null) {
            showTransactionDetail(selectedTransaction)
        }

        val isCancelled = cancelTransaction(selectedTransaction?.receiptId)
        if (isCancelled) {
            showToast("Transacción anulada con éxito")
        } else {
            showToast("Error al anular la transacción")
        }
    }

    private fun cancelTransaction(receiptId: String?): Boolean {
        val dbHelper = TransactionDatabase(requireContext())
        val transaction = receiptId?.let { dbHelper.getTransactionByReceiptId(it) }

        return if (transaction != null) {
            //transaction.isCancelled = true
            //dbHelper.updateTransaction(transaction)

            true
        } else {
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun searchTransaction(searchQuery: String) {
        if (searchQuery.equals("denegada", ignoreCase = true)) {
            val deniedTransactions = allTransactions.filter { it.statusDescription == "denegada" }
            adapter.updateData(deniedTransactions)
        } else {
            val adapter = MovementsAdapter(allTransactions) { selectedTransaction ->
                showAndCancelTransactionDetail(selectedTransaction)
            }

            val filteredTransactions = allTransactions.filter { transaction ->
                transaction.receiptId != null && transaction.receiptId.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }

            adapter.updateData(filteredTransactions)
            binding?.recyclerViewMovements?.adapter = adapter
        }

    }

    private fun showTransactionDetail(selectedTransaction: LocalDatabaseAuthorization) {
        selectedTransaction.let {
            it.receiptId?.let { it1 ->
                it.rrn?.let { it2 ->
                    it.statusDescription?.let { it3 ->
                        navigateToDetail(
                            it1,
                            it2, it3
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }

    private fun navigateToDetail(id: String, idSecond: String, status: String) {
        val intent = Intent(requireContext(), ItemViewDetailActivity::class.java)
        intent.putExtra(EXTRA_ID, id)
        intent.putExtra(EXTRA_IDSecond, idSecond)
        intent.putExtra(EXTRA_Status, status)
        startActivity(intent)
    }
}