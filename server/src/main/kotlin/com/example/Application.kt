package com.example

import com.example.internal.dbStorage.DBImageStorage
import com.example.internal.dbStorage.DBMessageStorage
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

fun Application.module(isTestMode: Boolean = false) {
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
    environment.config.property("database.url").getString(),
    driver = environment.config.property("database.driver").getString(),
    user = environment.config.propertyOrNull("database.username")?.getString().orEmpty(),
    password = environment.config.propertyOrNull("database.password")?.getString().orEmpty()
  )



  val userStorage = DBUserStorage(DBMaster.connection)
  userStorage.init()
  DBMaster.userStorage = userStorage

  val passwordStorage = DBPasswordStorage(DBMaster.connection)
  passwordStorage.init()
  DBMaster.passwordStorage = passwordStorage

  val messageStorage = DBMessageStorage(DBMaster.connection)
  messageStorage.init()
  DBMaster.messageStorage = messageStorage


  val imagesStorage = DBImageStorage(DBMaster.connection, issuer + "api/get_image?path=")
  imagesStorage.init()
  DBMaster.imagesStorage = imagesStorage

  configureSecurity()
  configureRouting(isTestMode)
}

object DBMaster {

  lateinit var connection: Database
  lateinit var passwordStorage: PasswordStorage
  lateinit var imagesStorage: ImageStorage
  lateinit var messageStorage: MessageStorage
  lateinit var userStorage: UserStorage

}
