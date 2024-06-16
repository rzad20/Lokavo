package com.lokavo.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ChatBotHistoryWithDetails(
    @Embedded val chatBotHistory: ChatBotHistory,
    @Relation(
        parentColumn = "id",
        entityColumn = "historyId"
    )
    val details: List<ChatBotHistoryDetail>
)