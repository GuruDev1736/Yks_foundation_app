package com.taskease.yksfoundation.Model

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val senderRole: String = "",
    val senderName: String = "",
    val profilePic: String = "",
    val designation : String = "",
    val timestamp: Long = 0L
)
