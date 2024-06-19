package com.lokavo.ui.historyChatbotDetail

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.lokavo.R
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.databinding.ActivityHistoryChatBotDetailBinding
import com.lokavo.ui.adapter.ChatAdapter
import com.lokavo.ui.adapter.Message
import com.lokavo.ui.chatbot.ChatBotActivity

class HistoryChatbotDetail : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryChatBotDetailBinding
    private lateinit var chatbotMessages: List<ChatBotMessageResponse>
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var userName: String
    private lateinit var photoUrl: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryChatBotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        chatbotMessages =
            intent.getParcelableArrayListExtra(ChatBotActivity.MESSAGES) ?: emptyList()
        userName = Firebase.auth.currentUser?.displayName ?: "User"
        photoUrl = Firebase.auth.currentUser?.photoUrl ?: Uri.EMPTY
        populateMessages()

        chatAdapter = ChatAdapter(messages)
        binding.rvMessages.adapter = chatAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
    }

    private fun populateMessages() {
        for (message in chatbotMessages) {
            messages.add(
                Message(
                    isUser = true,
                    user = userName,
                    bot = null,
                    text = message.question ?: "",
                    photo = photoUrl
                )
            )
            messages.add(
                Message(
                    isUser = false,
                    user = null,
                    bot = getString(R.string.lokavo_chatbot),
                    text = message.answer ?: "",
                    photo = null
                )
            )
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


    companion object {
        const val MESSAGES = "messages"
    }
}
