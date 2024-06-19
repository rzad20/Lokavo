package com.lokavo.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.lokavo.R
import com.lokavo.databinding.FragmentSecondOnboardingBinding

class SecondOnboarding : Fragment() {
    private var _binding: FragmentSecondOnboardingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondOnboardingBinding.inflate(inflater, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.nextText2.setOnClickListener {
            viewPager?.currentItem = 2
        }
        return binding.root
    }
}