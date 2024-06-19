package com.lokavo.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lokavo.databinding.FragmentViewPagerBinding
import com.lokavo.ui.adapter.ViewPagerAdapter
import com.lokavo.ui.onboarding.screens.FirstOnboarding
import com.lokavo.ui.onboarding.screens.SecondOnboarding
import com.lokavo.ui.onboarding.screens.ThirdOnboarding

class ViewPagerFragment : Fragment() {
    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val listFragment = arrayListOf(
            FirstOnboarding(),
            SecondOnboarding(),
            ThirdOnboarding()
        )
        val adapter = ViewPagerAdapter(
            listFragment,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = adapter
        return binding.root
    }
}