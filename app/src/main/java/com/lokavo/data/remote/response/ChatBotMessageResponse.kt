package com.lokavo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatBotMessageResponse(

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("error")
	val error: String? = null
): Parcelable
