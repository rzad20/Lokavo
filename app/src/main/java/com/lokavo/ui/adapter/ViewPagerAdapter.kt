package com.lokavo.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    list: ArrayList<Fragment>,
    frm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(frm, lifecycle) {
    private val listFragment: ArrayList<Fragment> = list

    override fun getItemCount(): Int {
        return listFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }
}