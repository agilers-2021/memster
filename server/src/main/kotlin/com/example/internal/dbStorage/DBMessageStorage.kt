package com.example.internal.dbStorage

import com.example.*
import com.example.models.ChatMessage
import com.example.models.SendMessageRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

class DBMessageStorage(val connection: Database) : MessageStorage {
    var nextId = 0
    object MessageTable : Table() {
        val id = integer("id").primaryKey()
        val text = text("text")
        val datetime = datetime("datetime")
        val senderId = integer("senderId")
        val receiverId = integer("receiverId")
        val imgId = integer("imageId").nullable()
    }

    fun init() {
        transaction(connection) {
            SchemaUtils.createMissingTablesAndColumns(MessageTable)
            nextId = MessageTable.selectAll().toList().size + 1
        }
    }

    override fun getChatsById(id: Int): List<Int> {
        return DBMaster.userStorage.getChatIds(id)
    }

    override fun getMessagesForChat(chatId: ChatId): List<ChatMessage> {
        val members = listOf<Int>(chatId.first, chatId.second)
        return transaction(connection) {
           MessageTable.select {
               ((MessageTable.senderId eq members[0]) or (MessageTable.senderId eq members[1])) and
                       ((MessageTable.receiverId eq members[0]) or (MessageTable.receiverId eq members[1]))
           }.map {
               ChatMessage(
                   it[MessageTable.id],
                   it[MessageTable.text],
                   it[MessageTable.datetime].toString(),
                   it[MessageTable.senderId],
                   it[MessageTable.receiverId],
                   it[MessageTable.imgId]
               )
           }
        }
    }

    override fun sendMessage(senderId: Int, message: SendMessageRequest) {
        val receiverId = DBMaster.userStorage.getUserId(message.receiver)!!
        var imgId: Int? = null
        if (message.image != null) {
            imgId = DBMaster.imagesStorage.putImage(Base64.getDecoder().decode(message.image))
        }
        transaction(connection) {
            MessageTable.insert {
                it[id] = nextId++
                it[MessageTable.senderId] = senderId
                it[MessageTable.receiverId] = receiverId
                it[text] = message.text
                it[datetime] = DateTime(System.currentTimeMillis())
                it[MessageTable.imgId] = imgId
            }
        }
    }
}