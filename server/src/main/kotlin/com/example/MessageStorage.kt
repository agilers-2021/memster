package com.example

import com.example.models.ChatMessage
import com.example.models.SendMessageRequest
import java.text.SimpleDateFormat
import kotlin.math.max
import kotlin.math.min


val FORMATTER: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z")

interface MessageStorage {
  fun getChatsById(id: Int): List<Int>

  fun getMessagesForChat(chatId: ChatId): List<ChatMessage>?

  fun sendMessage(senderId: Int, message: SendMessageRequest)
}

typealias ChatId = Participants

data class Participants(val first: Int, val second: Int) {
  override fun equals(other: Any?): Boolean {
    return if (other == null || other !is Participants) false
    else other.first == first && other.second == second
      || other.first == second && other.second == first
  }

  override fun hashCode(): Int {
    var result = max(first, second)
    result = 31 * result + min(first, second)
    return result
  }
}