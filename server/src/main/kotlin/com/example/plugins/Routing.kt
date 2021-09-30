package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.*
import com.example.internal.dummyRealization.InMemoryImageStorage
import com.example.models.*
import com.example.internal.dummyRealization.InMemoryUserStorage
import com.example.models.Credentials
import com.example.models.ErrorDescription
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*
import io.ktor.http.content.*
import io.ktor.util.pipeline.*

fun Application.configureRouting() {

  val storage: UserStorage = InMemoryUserStorage()
  val imageStorage: ImageStorage = InMemoryImageStorage()
//  val storage = DBMaster

  routing {
    route("/") {
      static {
        files("client")
      }

      get {
        call.respondRedirect("/login")
      }

      route ("login") {
        static {
          default("client/login.html")
        }
      }

      route ("register") {
        static {
          default("client/register.html")
        }
      }

      route ("user_info") {
        static {
          default("client/user_info.html")
        }
      }

      route("api") {
        route("authenticate") {
          post {
            val credentials = call.receive<Credentials>()

            //TODO - check credentials

            val token = JWT.create()
              .withAudience(audience)
              .withIssuer(issuer)
              .withClaim("username", credentials.username)
              .withExpiresAt(Date(System.currentTimeMillis() + 60000))
              .sign(Algorithm.HMAC256(secret))
            call.respond(TokenResponse(token))
          }
        }

        route("register") {
          post {
            val request = call.receive<RegisterRequest>()
            //TODO: handle passwords

            storage.putUser(
              request.username,
              UserObject(request.username, request.display_name ?: request.username, null)
            )

            val token = JWT.create()
              .withAudience(audience)
              .withIssuer(issuer)
              .withClaim("username",request.username)
              .withExpiresAt(Date(System.currentTimeMillis() + 60000))
              .sign(Algorithm.HMAC256(secret))
            call.respond(TokenResponse(token))
          }
        }

        authenticate("auth-jwt") {
          val getUserId: PipelineContext<Unit, ApplicationCall>.() -> Int = {
            val principal = call.principal<JWTPrincipal>()!!
            val username = principal.payload.getClaim("username").asString()
            val id = storage.getUserId(username) ?: error("no user with specified id found")
            id
          }

          route("settings") {
            //TODO: settings page
            post {
              val id = getUserId()
              val info = storage.getUserById(id) ?: error("user info not found")
              val request = call.receive<SettingsRequest>()
              if (request.delete_photo == true) {
                imageStorage.deleteImage(info.photoUrl!!)
              }
              var photoUrl = info.photoUrl
              if (request.set_photo != null) {
                photoUrl = imageStorage.putImage(info.username, Base64.getDecoder().decode(request.set_photo))
              }
              val newInfo = UserObject(info.username, request.display_name ?: info.displayName, photoUrl)
              storage.updateUser(id, newInfo)
              call.respond(HttpStatusCode.OK)
            }
          }

          get("user_info") {
            val id = getUserId()
            val info = storage.getUserById(id) ?: error("user info not found")
            call.respond(info)
          }
        }
      }
    }
  }
}
