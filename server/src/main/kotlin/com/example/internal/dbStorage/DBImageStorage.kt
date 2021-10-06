package com.example.internal.dbStorage

import com.example.ImageStorage
import com.example.internal.dbStorage.DBUserStorage.UserTable.nullable
import com.example.internal.dbStorage.DBUserStorage.UserTable.primaryKey
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DBImageStorage(val connection: Database, private val handlerUrl: String = "") : ImageStorage{

    private var nextId = 1

    object ImagesTable : Table() {
        val path = integer("id").primaryKey()
        val image = binary("image", 100000)
    }

    fun init() {
        transaction(connection) {
            SchemaUtils.create(ImagesTable)
            nextId = ImagesTable.selectAll().toList().size + 1
        }

    }
    override fun putImage(username: String, image: ByteArray): String {
        return transaction(connection) {
            ImagesTable.insert {
                it[path] = nextId
                nextId += 1
                it[ImagesTable.image] = image
            }
            (nextId - 1).toString()
        }
    }

    override fun getImage(path: String): ByteArray? {
        return transaction(connection) {
            ImagesTable.select { ImagesTable.path eq path.toInt() }.singleOrNull()?.let {
                it[ImagesTable.image]
            }
        }
    }

    override fun getLink(path: String?): String? {
        return if (path != null) handlerUrl + path else null
    }

    override fun deleteImage(path: String) {
        transaction(connection) {
            ImagesTable.deleteWhere { ImagesTable.path eq path.toInt() }
        }
    }
}