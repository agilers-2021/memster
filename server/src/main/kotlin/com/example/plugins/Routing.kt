package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.*
import com.example.internal.dummyRealization.InMemoryImageStorage
import com.example.internal.dummyRealization.InMemoryUserStorage
import com.example.models.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*
import io.ktor.http.content.*

fun Application.configureRouting() {

  val storage: UserStorage = InMemoryUserStorage()
  val imageStorage: ImageStorage = InMemoryImageStorage()

  routing {
    route("/") {
      static {
        files("client")
      }

      get {
        call.respondRedirect("/api/authenticate")
      }

      route ("create_account") {
        static {
          default("client/create_user.html")
          files("client")
        }
      }

      route("api") {
        static {
          files("client")
        }
        route("authenticate") {
          static {
            default("client/login.html")
            files("client")
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

        route("register") {
          static {
            default("client/create_user.html")  // TODO: Change to page with just login and password
          }
          post {
            val request = call.receive<RegisterRequest>()
            //TODO: handle passwords

            storage.putUser(request.username,
                            UserObject(request.display_name ?: request.username, null))

            val token = JWT.create()
              .withAudience(audience)
              .withIssuer(issuer)
              .withClaim("username",request.username)
              .withExpiresAt(Date(System.currentTimeMillis() + 60000))
              .sign(Algorithm.HMAC256(secret))
            call.respond(hashMapOf("token" to token))
          }
        }

        authenticate("auth-jwt") {
          route("settings") {
            //TODO: settings page
            post {
              val principal = call.principal<JWTPrincipal>()
              val username = principal!!.payload.getClaim("username").asString()
              val id = storage.getUserId(username) ?: let {
                val response = createFailureResponse(ErrorDescription("weird"))
                return@post call.respond(response)
              }
              val info = storage.getUserById(id) ?: let {
                val response = createFailureResponse(ErrorDescription("weird"))
                return@post call.respond(response)
              }
              val request = call.receive<SettingsRequest>()
              if (request.delete_photo == true) {
                imageStorage.deleteImage(info.photoUrl!!)
              }
              var photoUrl = info.photoUrl
              if (request.set_photo != null) {
                photoUrl = imageStorage.putImage(username, Base64.getDecoder().decode(request.set_photo))
              }
              val newInfo = UserObject(request.display_name ?: info.displayName, photoUrl)
              storage.updateUser(id, newInfo)
              call.respond(HttpStatusCode.OK)
            }
          }

          route("user_info") {
            static {
              default("client/user_page.html")
            }


            post {
              val params = call.receiveParameters()
              val login = params["login"]
              val password = params["password"]
              val repeat_password = params["repeat-password"]
              return@post call.respond(HttpStatusCode.OK)
//              val principal = call.principal<JWTPrincipal>()
//              val username = principal!!.payload.getClaim("username").asString()
//              val id = storage.getUserId(username) ?: let {
//                val response = createFailureResponse(ErrorDescription("weird"))
//                return@post call.respond(response)
//              }
//              val info = storage.getUserById(id) ?: let {
//                val response = createFailureResponse(ErrorDescription("weird"))
//                return@post call.respond(response)
//              }
//              call.respond(createSuccessResponse(info))
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

