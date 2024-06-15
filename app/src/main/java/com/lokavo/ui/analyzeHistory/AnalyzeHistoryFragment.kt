package com.lokavo.ui.analyzeHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.lokavo.databinding.FragmentAnalyzeHistoryBinding
import com.lokavo.ui.adapter.AnalyzeHistoryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnalyzeHistoryFragment : Fragment() {
    private var _binding: FragmentAnalyzeHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AnalyzeHistoryAdapter
    private val analyzeHistoryViewModel: AnalyzeHistoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeHistoryBinding.inflate(inflater, container, false)
        setupRecyclerView()
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            observeHistory(userId)
        }
        binding.btnClear.setOnClickListener {
            if (userId != null) {
                analyzeHistoryViewModel.deleteAll(userId)
            }
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = AnalyzeHistoryAdapter(analyzeHistoryViewModel)
        with(binding.rvHistory) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
            setHasFixedSize(true)
            adapter = this@AnalyzeHistoryFragment.adapter
        }
    }

    private fun observeHistory(userId: String) {
        analyzeHistoryViewModel.getAll(userId).observe(viewLifecycleOwner) { historyList ->
            historyList?.let { adapter.setListHistory(it) }
            if (adapter.itemCount == 0) {
                binding.tvEmptyHistory.visibility = View.VISIBLE
            } else {
                binding.tvEmptyHistory.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
