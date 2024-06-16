package com.lokavo.ui.chatbot

import android.annotation.SuppressLint
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.data.local.entity.ChatBotHistoryDetail
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.databinding.ActivityChatBotBinding
import com.lokavo.ui.adapter.ChatAdapter
import com.lokavo.ui.adapter.Message
import com.lokavo.ui.chatBotHistory.ChatBotHistoryViewModel
import com.lokavo.utils.DateFormatter
import com.lokavo.utils.getAddress
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbar
import com.lokavo.utils.showSnackbarOnNoConnection
import kotlinx.coroutines.launch
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatBotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBotBinding
    private val messages = mutableListOf<Message>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatbotMessages: List<ChatBotMessageResponse>
    private var currentIndex = 0
    private lateinit var userNane: String
    private lateinit var photoUrl: Uri
    private lateinit var uid: String
    private lateinit var latLng: LatLng
    private val geocoder by lazy { Geocoder(this, Locale.getDefault()) }
    private val chatBotHistoryViewModel: ChatBotHistoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        latLng = intent.getParcelableExtra(LOCATION) ?: LatLng(0.0, 0.0)
        chatbotMessages = intent.getParcelableArrayListExtra(MESSAGES) ?: emptyList()
        userNane = Firebase.auth.currentUser?.displayName ?: "User"
        photoUrl = Firebase.auth.currentUser?.photoUrl ?: Uri.EMPTY
        uid = Firebase.auth.currentUser?.uid ?: ""

        chatAdapter = ChatAdapter(messages)
        binding.rvMessages.adapter = chatAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(this)

        if (chatbotMessages.isNotEmpty()) {
            binding.btnNext.text = chatbotMessages[currentIndex].question
        } else {
            binding.messageChoice.visibility = View.GONE
            binding.root.showSnackbar("Terjadi Kesalahan")
        }

        binding.btnNext.setOnClickListener {
            if (!this.isOnline()) {
                binding.root.showSnackbarOnNoConnection(this)
            } else {
                nextQuestion()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun nextQuestion() {
        if (currentIndex < chatbotMessages.size) {
            val question = chatbotMessages[currentIndex].question
            val answer = chatbotMessages[currentIndex].answer

            messages.add(
                Message(
                    isUser = true,
                    user = userNane,
                    bot = null,
                    text = question ?: "",
                    photo = photoUrl
                )
            )
            messages.add(
                Message(
                    isUser = false,
                    user = null,
                    bot = "ChatBot",
                    text = answer ?: "",
                    photo = null
                )
            )
            chatAdapter.notifyDataSetChanged()
            binding.rvMessages.scrollToPosition(messages.size - 1)

            currentIndex++
            if (currentIndex < chatbotMessages.size) {
                binding.btnNext.text = chatbotMessages[currentIndex].question
            } else {
                binding.messageChoice.visibility = View.GONE
                saveChatHistory()            }
        }
    }

    private fun saveChatHistory() {
        val chatBotHistoryDetails = chatbotMessages.map { message ->
            ChatBotHistoryDetail(
                question = message.question ?: "",
                answer = message.answer ?: ""
            )
        }
        lifecycleScope.launch {
            val lat = latLng.latitude
            val long = latLng.longitude
            val address = geocoder.getAddress(lat, long)
            val addressName = address?.getAddressLine(0) ?: "${lat},${long}"
            chatBotHistoryViewModel.insertOrUpdate(
                uid,
                ChatBotHistory(
                    userId = uid,
                    latitude = lat,
                    longitude = long,
                    address = addressName,
                    date = DateFormatter.getCurrentDate()
                ),
                chatBotHistoryDetails
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
        const val LOCATION = "location"
        const val MESSAGES = "messages"
    }
}
