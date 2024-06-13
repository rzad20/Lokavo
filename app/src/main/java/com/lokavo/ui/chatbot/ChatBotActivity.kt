package com.lokavo.ui.chatbot

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.lokavo.data.Result
import com.lokavo.databinding.ActivityChatBotBinding
import com.lokavo.ui.adapter.ChatAdapter
import com.lokavo.ui.adapter.Message
import com.lokavo.utils.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatBotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBotBinding
    private val viewModel: ChatBotViewModel by viewModel()
    private var currentQuestionIndex = 1

    private val messages = mutableListOf<Message>()
    private lateinit var chatAdapter: ChatAdapter
    private var userText = ""
    private var botText = ""
    private var visible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        chatAdapter = ChatAdapter(messages)

        binding.rvMessages.adapter = chatAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(this)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null && messages.isEmpty()) {
            getChatBotMessage(uid, currentQuestionIndex)
        }
        binding.btnNext.setOnClickListener {
            if (uid != null) {
                binding.messageChoice.visibility = View.GONE
                messages.add(
                    Message(
                        user = user.displayName,
                        text = userText,
                        isUser = true,
                        bot = null,
                        photo = user.photoUrl
                    )
                )
                chatAdapter.notifyItemInserted(messages.size - 1)

                messages.add(
                    Message(
                        user = null,
                        text = botText,
                        isUser = false,
                        bot = "Chatbot",
                        photo = null
                    )
                )
                chatAdapter.notifyItemInserted(messages.size - 1)
                if (currentQuestionIndex < 3) {
                    currentQuestionIndex++
                    getChatBotMessage(uid, currentQuestionIndex)
                }
            }
        }
    }

    private fun getChatBotMessage(uid: String, index: Int) {
        Log.i("call", index.toString())
        viewModel.getChatBotMessage(uid, index).observe(this) { res ->
            when (res) {
                is Result.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.btnNext.text = ""
                    binding.messageChoice.visibility = View.GONE
                }

                is Result.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.root.showSnackbar(res.error)
                }

                is Result.Success -> {
                    res.data.answer?.let {
                        botText = it
                    }
                    res.data.question?.let {
                        userText = it
                    }
                    binding.progress.visibility = View.GONE
                    binding.btnNext.text = res.data.question
                    binding.messageChoice.visibility = View.VISIBLE
                }

                else -> {}
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> false
        }
    }
}
