package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_capsules")
data class MemoryCapsule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val videoUrl: String,
    val category: String, // e.g. "Sunset", "Friends", "Nostalgia", "Quiet", "Action"
    val emotionTag: String, // Arabic emotion: "هدوء", "حنين", "دفء", "تأمل", "حماس"
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val moodPace: String // "peaceful" or "action" (controls Mood Echo animation speed)
)

@Entity(tableName = "friend_bubbles")
data class FriendBubble(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val avatarUrl: String,
    val colorHex: String, // Hex string e.g. "#FF5733"
    val shapeType: String, // "Circle", "Hexagon", "RoundedRect", "Bubble"
    val isOnline: Boolean,
    val latestMessage: String,
    val channelName: String // associated YouTube/media channel
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val friendId: Int,
    val senderName: String, // "user", "friend", or "AI"
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)
