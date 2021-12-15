package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*

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
//    DBMaster.connection = Database.connect(
//        environment.config.property("database.url").getString(),
//        driver = environment.config.property("database.driver").getString(),
//        user = environment.config.propertyOrNull("database.username")?.getString().orEmpty(),
//        password = environment.config.propertyOrNull("database.password")?.getString().orEmpty()
//    )

//    val imagesStorage = DBImageStorage(DBMaster.connection, issuer + "api/get_image?id=")
//    imagesStorage.init()
//    DBMaster.imagesStorage = imagesStorage

    configureSecurity()
    configureRouting()
}

//object DBMaster {
//
//    lateinit var connection: Database
//    lateinit var passwordStorage: PasswordStorage
//    lateinit var imagesStorage: ImageStorage
//    lateinit var messageStorage: MessageStorage
//    lateinit var userStorage: UserStorage
//
//}
