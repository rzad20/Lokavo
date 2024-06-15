package com.lokavo.ui.searchHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lokavo.databinding.FragmentHistoryBinding
import com.lokavo.databinding.FragmentSearchHistoryBinding
import com.lokavo.ui.adapter.HistoryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchHistoryFragment : Fragment() {
    private var _binding: FragmentSearchHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HistoryAdapter
    private val searchHistoryViewModel: SearchHistoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchHistoryBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeHistory()
        binding.btnClear.setOnClickListener {
            searchHistoryViewModel.deleteAll()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(searchHistoryViewModel)
        with(binding.rvHistory) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
            setHasFixedSize(true)
            adapter = this@SearchHistoryFragment.adapter
        }
    }

    private fun observeHistory() {
        searchHistoryViewModel.getAll().observe(viewLifecycleOwner) { historyList ->
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
