package com.example.internal.dbStorage

import com.example.ChatId
import com.example.MessageStorage
import com.example.UserStorage
import com.example.internal.dbStorage.DBPasswordStorage.PasswordTable.primaryKey
import com.example.models.ChatMessage
import com.example.models.SendMessageRequest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

class DBMessageStorage(val connection: Database) : MessageStorage {
    object MessageTable : Table() {
        val id = integer("id")
        val text = text("text")
        val datetime = datetime("datetime")
        val sender = varchar("sender", length=100)
        val receiver = varchar("varchar", length=100)
    }

    fun init() {
        transaction(connection) {
            SchemaUtils.createMissingTablesAndColumns(DBMessageStorage.MessageTable)
        }
    }

    override fun getChatsById(id: Int): List<Int> {
//        transaction(connection) {
//            connection.
//        }
        TODO("Not yet implemented")
    }

    override fun getMessagesForChat(chatId: ChatId): List<ChatMessage>? {
        TODO("Not yet implemented")
    }

    override fun sendMessage(senderId: Int, message: SendMessageRequest) {
        TODO("Not yet implemented")
    }
}