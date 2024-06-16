package com.lokavo.ui.chatBotHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.lokavo.databinding.FragmentChatBotHistoryBinding
import com.lokavo.ui.adapter.ChatBotHistoryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatBotHistoryFragment : Fragment() {
    private var _binding: FragmentChatBotHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChatBotHistoryAdapter
    private val chatBotistoryViewModel: ChatBotHistoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBotHistoryBinding.inflate(inflater, container, false)
        setupRecyclerView()
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            observeHistory(userId)
        }
        binding.btnClear.setOnClickListener {
            if (userId != null) {
                chatBotistoryViewModel.deleteAll(userId)
            }
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ChatBotHistoryAdapter(chatBotistoryViewModel)
        with(binding.rvHistory) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
            setHasFixedSize(true)
            adapter = this@ChatBotHistoryFragment.adapter
        }
    }

    private fun observeHistory(userId: String) {
        chatBotistoryViewModel.getAll(userId).observe(viewLifecycleOwner) { historyList ->
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
