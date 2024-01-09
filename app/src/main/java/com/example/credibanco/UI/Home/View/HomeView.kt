package com.example.credibanco.UI.Home.View

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.credibanco.UI.Transaction.View.TransactionView
import com.example.credibanco.databinding.FragmentHomeViewBinding

class HomeView : Fragment() {

    private var binding: FragmentHomeViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeViewBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateToDataTransaccion()
    }

    private fun navigateToDataTransaccion() {
        binding?.CVTransaction?.setOnClickListener {
            val intent = Intent(requireContext(), TransactionView::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }
}