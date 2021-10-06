package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.*
import com.example.internal.dbStorage.DBPasswordStorage
import com.example.internal.dummyRealization.DummyPasswordStorage
import com.example.internal.dummyRealization.InMemoryImageStorage
import com.example.models.*
import com.example.models.Credentials
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*
import io.ktor.http.content.*
import io.ktor.util.pipeline.*
import java.io.File
import java.nio.file.Files

fun Application.configureRouting() {

//  val storage: UserStorage = InMemoryUserStorage()
  val imageStorage: ImageStorage = InMemoryImageStorage(issuer + "api/get_image?path=")
  val storage = DBMaster
  val passwordStorage: PasswordStorage = DummyPasswordStorage()

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

      route ("edit") {
        static {
          default("client/edit.html")
        }
      }

      route("api") {
        route("authenticate") {
          post {
            val credentials = call.receive<Credentials>()

            when(storage.passwordStorage.checkCredentials(credentials)) {
              CredentialsCheckResult.INVALID_CREDENTIALS -> {
                call.respondText("Invalid username or password", status = HttpStatusCode.Unauthorized)
                return@post
              }
              else -> {}
            }

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

            storage.passwordStorage.storeCredentials(Credentials(request.username, request.password))

            storage.putUser(
              request.username,
              UserObject(request.username, request.display_name ?: request.username, null, "kolobok umer")
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

        route("get_image"){
          get {
            val path = call.parameters["path"]
            if (path == null) {
              call.respond(HttpStatusCode.BadRequest, "Wrong path")
              return@get
            }
            val img = imageStorage.getImage(path)
            if (img == null) {
              call.respond(HttpStatusCode.BadRequest)
              return@get
            }
            call.respondBytes(img)
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
            post {
              val id = getUserId()
              val info = storage.getUserById(id) ?: error("user info not found")
              val request = call.receive<SettingsRequest>()
              var photoUrl = info.photoUrl
              if (request.set_photo != null) {
                photoUrl = imageStorage.putImage(info.username, Base64.getDecoder().decode(request.set_photo))
              }
              if (request.delete_photo == true && info.photoUrl != null) {
                imageStorage.deleteImage(info.photoUrl)
                photoUrl = null
              }
              val newInfo = UserObject(info.username, request.display_name ?: info.displayName, photoUrl, "kolobok umer")
              storage.updateUser(id, newInfo)
              call.respond(HttpStatusCode.OK)
            }
          }

          get("user_info") {
            val id = getUserId()
            val info = storage.getUserById(id) ?: error("user info not found")
            call.respond(UserObject(info.username, info.displayName,
               imageStorage.getLink(info.photoUrl), info.anecdote))
          }
        }
      }
    }
  }
}
