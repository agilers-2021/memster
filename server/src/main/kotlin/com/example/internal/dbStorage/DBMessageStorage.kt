package com.example.internal.dbStorage

import com.example.*
import com.example.models.ChatMessage
import com.example.models.SendMessageRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class DBMessageStorage(val connection: Database) : MessageStorage {
    var nextId = 0
    object MessageTable : Table() {
        val id = integer("id").primaryKey()
        val text = text("text")
        val datetime = datetime("datetime")
        val senderId = integer("senderId")
        val receiverId = integer("receiverId")
    }

    fun init() {
        transaction(connection) {
            SchemaUtils.createMissingTablesAndColumns(MessageTable)
            nextId = MessageTable.selectAll().toList().size + 1
        }
    }

    override fun getChatsById(id: Int): List<Int> {
        println(id)
        return transaction(connection) {
            MessageTable.select { (MessageTable.senderId eq id) or (MessageTable.receiverId eq id)}.map {
                // return one of two possible ids which is not id
                it[MessageTable.senderId] + it[MessageTable.receiverId] - id
            }
        }
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
                   it[MessageTable.receiverId]
               )
           }
        }
    }

    override fun sendMessage(senderId: Int, message: SendMessageRequest) {
        val receiverId = DBMaster.userStorage.getUserId(message.receiver)!!
        transaction(connection) {
            MessageTable.insert {
                it[id] = nextId++
                it[MessageTable.senderId] = senderId
                it[MessageTable.receiverId] = receiverId
                it[text] = message.text
                it[datetime] = DateTime(System.currentTimeMillis())
            }
        }
    }
}