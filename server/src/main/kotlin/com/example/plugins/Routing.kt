package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.UserStorage
import com.example.audience
import com.example.internal.dummyRealization.InMemoryUserStorage
import com.example.issuer
import com.example.models.Credentials
import com.example.models.ErrorDescription
import com.example.models.createFailureResponse
import com.example.models.createSuccessResponse
import com.example.secret
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*
import io.ktor.http.content.*

fun Application.configureRouting() {

  val storage: UserStorage = InMemoryUserStorage()

  routing {
    route("/") {
      static {
        files("client")
      }

      get {
        call.respondRedirect("/api/authenticate")
      }

      static("create_account") {
        default("client/create_user.html")
      }

      route("api") {
        route("authenticate") {
          static {
            default("client/login.html")
          }
          post {
            val credentials = call.receive<Credentials>()

            //TODO - check credentials

            val token = JWT.create()
              .withAudience(audience)
              .withIssuer(issuer)
              .withClaim("username", credentials.username)
              .withExpiresAt(Date(System.currentTimeMillis() + 60000))
              .sign(Algorithm.HMAC256(secret))
            call.respond(hashMapOf("token" to token))
          }
        }

        authenticate("auth-jwt") {
          route("user_info") {
            static {
              default("client/user_page.html")
            }

            get {
              val principal = call.principal<JWTPrincipal>()
              val username = principal!!.payload.getClaim("username").asString()
              val id = storage.getUserId(username) ?: let {
                val response = createFailureResponse(ErrorDescription("weird"))
                return@get call.respond(response)
              }
              val info = storage.getUserById(id) ?: let {
                val response = createFailureResponse(ErrorDescription("weird"))
                return@get call.respond(response)
              }
              call.respond(createSuccessResponse(info))
            }
          }
        }

        get("user_info/{id}") {
          val id = call.parameters["id"]?.toIntOrNull() ?: let {
            val response = createFailureResponse(ErrorDescription("invalid id supplied"))
            return@get call.respond(response)
          }
          val info = storage.getUserById(id) ?: let {
            val response = createFailureResponse(ErrorDescription("no user with that id found"))
            return@get call.respond(response)
          }
          call.respond(createSuccessResponse(info))
        }
      }
    }
  }
}

