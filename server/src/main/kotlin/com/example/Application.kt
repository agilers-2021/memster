package com.example

import com.example.plugins.*
import io.ktor.application.*
import io.ktor.features.*
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
  configureSecurity()
  configureRouting()
}
