package com.example.internal.dummyRealization

import com.example.*
import com.example.models.ChatMessage
import com.example.models.SendMessageRequest
import java.util.*

class InMemoryMessageStorage(private val userStorage: UserStorage) : MessageStorage {
  private val storage = hashMapOf<Participants, MutableList<ChatMessage>>()

  var nextId = 0;

  override fun getChatsById(id: Int): List<Int> {
    val result = mutableListOf<Int>()
    for ((participants, _) in storage) {
      if (id == participants.first) {
        result.add(participants.second)
      } else if (id == participants.second) {
        result.add(participants.first)
      }
    }
    return result
  }

  override fun getMessagesForChat(chatId: ChatId): List<ChatMessage>? {
    return storage[chatId]?.toList()
  }

  override fun sendMessage(senderId: Int, message: SendMessageRequest) {
    val receiverId = userStorage.getUserId(message.receiver) ?: error("invalid receiver username")
    val senderName = userStorage.getUserById(senderId)?.username ?: error("weird")
    val chatId = ChatId(senderId, receiverId)
    storage.putIfAbsent(chatId, mutableListOf())
    storage[chatId]?.add(
      ChatMessage(
        nextId,
        message.text,
        FORMATTER.format(Date(System.currentTimeMillis())),
        senderName,
        message.receiver
      )
    )
    nextId++
  }
}