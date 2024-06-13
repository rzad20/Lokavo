package com.lokavo.data.remote.response

import com.google.gson.annotations.SerializedName

data class ChatBotMessageResponse(

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("error")
	val error: String? = null
)
