package com.lokavo.ui.historyChatbotDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lokavo.databinding.ActivityHistoryChatBotDetailBinding


class HistoryChatbotDetail : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryChatBotDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryChatBotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}