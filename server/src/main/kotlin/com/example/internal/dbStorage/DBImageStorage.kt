package com.example.internal.dbStorage

import com.example.ImageStorage
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DBImageStorage(val connection: Database, private val handlerUrl: String = "") : ImageStorage{

    private var nextId = 1

    object ImagesTable : Table() {
        val id = integer("id").primaryKey()
        val image = binary("image", 10000000)
    }

    fun init() {
        transaction(connection) {
            SchemaUtils.create(ImagesTable)
            nextId = ImagesTable.selectAll().toList().size + 1
        }

    }
    override fun putImage(image: ByteArray): Int {
        return transaction(connection) {
            ImagesTable.insert {
                it[id] = nextId
                nextId += 1
                it[ImagesTable.image] = image
            }
            nextId - 1
        }
    }

    override fun getImage(id: Int): ByteArray? {
        return transaction(connection) {
            ImagesTable.select { ImagesTable.id eq id }.singleOrNull()?.let {
                it[ImagesTable.image]
            }
        }
    }

    override fun getLink(path: String?): String? {
        return if (path != null) handlerUrl + path else null
    }

    override fun deleteImage(id: Int) {
        transaction(connection) {
            ImagesTable.deleteWhere { ImagesTable.id eq id }
        }
    }
}