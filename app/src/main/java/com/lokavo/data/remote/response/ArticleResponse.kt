package com.lokavo.data.remote.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("list")
	val list: List<ListItem?>? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ListItem(

	@field:SerializedName("Gambar")
	val gambar: String? = null,

	@field:SerializedName("Ringkasan")
	val ringkasan: String? = null,

	@field:SerializedName("Headline")
	val headline: String? = null,

	@field:SerializedName("Link")
	val link: String? = null
)
