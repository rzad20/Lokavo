package com.lokavo.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lokavo.ui.searchHistory.SearchHistoryFragment
import com.lokavo.ui.chatbotHistory.ChatbotHistoryFragment

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchHistoryFragment()
            1 -> ChatbotHistoryFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
