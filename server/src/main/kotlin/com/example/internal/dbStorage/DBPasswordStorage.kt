package com.example.internal.dbStorage

import org.jetbrains.exposed.sql.*
import com.example.CredentialsCheckResult
import com.example.PasswordStorage
import com.example.models.Credentials
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt;


class DBPasswordStorage(val connection: Database): PasswordStorage {
    object PasswordTable : Table() {
        val username = varchar("username", length=100).primaryKey()
        val passwordHash = varchar("password_hash", length=1024)
    }

    fun init() {
        transaction(connection) {
            SchemaUtils.createMissingTablesAndColumns(PasswordTable)
        }
    }

    override fun storeCredentials(credentials: Credentials) {
        transaction(connection) {
            PasswordTable.insertOrUpdate {
                it[username] = credentials.username
                it[passwordHash] = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
            }
        }
    }

    override fun checkCredentials(credentials: Credentials): CredentialsCheckResult {
        return transaction(connection) {
            PasswordTable.select {
                PasswordTable.username eq credentials.username
            }.singleOrNull()?.let {
                if (BCrypt.checkpw(credentials.password, it[PasswordTable.passwordHash])) {
                    return@transaction CredentialsCheckResult.SUCCESS
                }
            }

            return@transaction CredentialsCheckResult.INVALID_CREDENTIALS
        }
    }
}