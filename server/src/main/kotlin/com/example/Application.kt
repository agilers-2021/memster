package com.example

import com.example.internal.dbStorage.DBImageStorage
import com.example.internal.dbStorage.DBPasswordStorage
import com.example.internal.dbStorage.DBUserStorage
import com.example.models.UserObject
import com.example.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
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
  install(CORS) {
    method(HttpMethod.Options)
    method(HttpMethod.Put)
    method(HttpMethod.Delete)
    method(HttpMethod.Patch)
    header(HttpHeaders.Authorization)
    header(HttpHeaders.ContentType)
    // header("any header") if you want to add any header
    allowCredentials = true
    allowNonSimpleContentTypes = true
    anyHost()
  }
  install(CallLogging)
  DBMaster.connection = Database.connect(
    "jdbc:h2:./testdb",
    driver = "org.h2.Driver"
  )

  transaction {
    DBUserStorage.init()
  }

  val passwordStorage = DBPasswordStorage(DBMaster.connection)
  passwordStorage.init()
  DBMaster.passwordStorage = passwordStorage

  val imagesStorage = DBImageStorage(DBMaster.connection, issuer + "api/get_image?path=")
  imagesStorage.init()
  DBMaster.imagesStorage = imagesStorage

  configureSecurity()
  configureRouting()
}

object DBMaster {

  lateinit var connection: Database
  lateinit var passwordStorage: PasswordStorage
  lateinit var imagesStorage: ImageStorage

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
