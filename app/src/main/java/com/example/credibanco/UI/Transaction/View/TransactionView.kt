package com.example.credibanco.UI.Transaction.View

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import com.example.credibanco.Data.DataBase.TransactionDatabase
import com.example.credibanco.Data.Model.AuthorizationDataModel
import com.example.credibanco.Data.Model.LocalDatabaseAuthorization
import com.example.credibanco.R
import com.example.credibanco.UI.Transaction.ViewModel.TransactionViewModel
import com.example.credibanco.databinding.ActivityTransactionViewBinding
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.ThreadLocalRandom.current

class TransactionView : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionViewBinding
    private val transactionViewModel: TransactionViewModel by viewModels()

    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authhorizationState()
        setUpValueNext()
        setUpValueAuthorization()

    }

    private fun authhorizationState() {
        transactionViewModel.authorizationState.observe(this) { authorizationState ->
            when (authorizationState) {
                TransactionViewModel.AuthorizationState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.lLViewTransaction.visibility = View.INVISIBLE
                    binding.lLViewAuth.visibility = View.INVISIBLE
                }

                TransactionViewModel.AuthorizationState.SUCCESS -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.lLViewTransaction.visibility = View.INVISIBLE
                    binding.lLViewAuth.visibility = View.VISIBLE
                }

                TransactionViewModel.AuthorizationState.ERROR -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.lLViewTransaction.visibility = View.VISIBLE
                    binding.lLViewAuth.visibility = View.INVISIBLE
                }
            }

        }
    }

    private fun setUpValueAuthorization(){
        binding.btnNext.setOnClickListener {
            val userName = binding.etUserName.text.toString()
            val code = binding.etPassword.text.toString()
            transactionViewModel.paymentApiConnectAnnulation(this, userName, code)
        }
    }

    private fun setUpValueNext() {
        binding.btnBuy.setOnClickListener {
            val commerceCode = binding.etCodeCommerce.text.toString()
            val terminalCode = binding.etCodeTerminal.text.toString()
            val mount = binding.etAmount.text.toString()
            val card = binding.etNumberCard.text.toString()
            transactionViewModel.paymentApiConnect(this ,commerceCode,terminalCode,mount,card)
            binding.lLViewTransaction.visibility = View.INVISIBLE
            binding.lLViewAuth.visibility = View.VISIBLE
        }
    }


    private fun formatCurrency(editable: Editable?, editText: EditText) {
        try {
            val value = currencyFormat.parse(editable.toString())

            editText.removeTextChangedListener(editText.tag as TextWatcher)
            editText.setText(currencyFormat.format(value))
            editText.setSelection(editText.text?.length ?: 0)
            editText.addTextChangedListener(editText.tag as TextWatcher)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}