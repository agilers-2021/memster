package com.example

import com.example.internal.dbStorage.DBUserStorage
import com.example.models.UserObject
import com.example.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val Application.secret: String
  get() = environment.config.property("jwt.secret").getString()

val Application.issuer: String
  get() = environment.config.property("jwt.issuer").getString()

val Application.audience: String
  get() = environment.config.property("jwt.audience").getString()

val Application.myRealm: String
  get() = environment.config.property("jwt.realm").getString()

fun Application.module() {
  install(ContentNegotiation) {
    json()
  }
  install(CallLogging)
  DBMaster.connection = Database.connect(
    "jdbc:h2:./testdb",
    driver = "org.h2.Driver"
  )
  transaction {
    DBUserStorage.init()
  }
  configureSecurity()
  configureRouting()
}

object DBMaster {

  lateinit var connection: Database

  fun putUser(username: String, userObject: UserObject) =
    transaction(connection) {
      val id = DBUserStorage.putUser(username, userObject)
    }

  fun getUserId(username: String): Int? {
    return transaction(connection) {
      DBUserStorage.getUserId(username)
    }
  }

  fun getUserById(id: Int):UserObject? {
    return transaction(connection) {
      DBUserStorage.getUserById(id)
    }
  }

  fun updateUser(id: Int, user: UserObject) {
    return transaction(connection) {
      DBUserStorage.updateUser(id, user)
    }
  }
}
