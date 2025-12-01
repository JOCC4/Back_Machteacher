package com.example.machteacher.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val conversationId: Long,
    val senderId: Long,
    val body: String,
    val createdAt: String
)