package com.lokavo.ui.historyChatbotDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lokavo.databinding.ActivityHistoryChatbotDetailBinding


class HistoryChatbotDetail : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryChatbotDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryChatbotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}