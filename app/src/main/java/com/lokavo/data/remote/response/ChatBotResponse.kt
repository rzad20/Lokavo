package com.lokavo.data.remote.response

import com.google.gson.annotations.SerializedName

data class ChatBotResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
