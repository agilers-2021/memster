package com.example.plugins

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {

  routing {
    get("/") {
      // TODO check auth
      call.respondRedirect("/login")
    }
    static {
      files("client")
    }
    static("/create_account") {
      default("client/create_user.html")
    }
    static("/login") {
      default("client/login.html")
    }
    static("/me") {
      default("client/user_page.html")
    }
  }
}
