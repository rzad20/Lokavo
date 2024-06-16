package com.lokavo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_bot_history_detail",
    foreignKeys = [ForeignKey(
        entity = ChatBotHistory::class,
        parentColumns = ["id"],
        childColumns = ["historyId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["historyId"])]
)
data class ChatBotHistoryDetail(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    @ColumnInfo(name = "historyId")
    var historyId: Long? = null,

    @ColumnInfo(name = "question")
    var question: String? = null,

    @ColumnInfo(name = "answer")
    var answer: String? = null
)