package com.lokavo.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lokavo.ui.analyzeHistory.AnalyzeHistoryFragment
import com.lokavo.ui.chatBotHistory.ChatBotHistoryFragment

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AnalyzeHistoryFragment()
            1 -> ChatBotHistoryFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
