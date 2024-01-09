package com.example.credibanco.UI.Movements.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.credibanco.R
import com.example.credibanco.databinding.ActivityItemViewDetailBinding

class ItemViewDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemViewDetailBinding

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_IDSecond = "extra_idsecond"
        const val EXTRA_Status = "extra_status"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemViewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvNumberID.text = intent.getStringExtra(EXTRA_ID).orEmpty()
        binding.tvNumberIDSegond.text = intent.getStringExtra(EXTRA_IDSecond).orEmpty()
        binding.tvStatus.text = intent.getStringExtra(EXTRA_Status).orEmpty()
    }
}