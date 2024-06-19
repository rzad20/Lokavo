package com.lokavo.ui.onboarding.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lokavo.R
import com.lokavo.databinding.FragmentThirdOnboardingBinding

class ThirdOnboarding : Fragment() {
    private var _binding: FragmentThirdOnboardingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdOnboardingBinding.inflate(inflater, container, false)
        binding.finishText.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_welcomeFragment)
            finishOnboarding()
        }
        return binding.root
    }

    private fun finishOnboarding() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}