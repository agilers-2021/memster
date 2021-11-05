package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.*
import com.example.internal.dummyRealization.InMemoryImageStorage
import com.example.internal.dummyRealization.InMemoryMessageStorage
import com.example.internal.dummyRealization.InMemoryUserStorage
import com.example.models.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import java.util.*

fun Application.configureRouting(isTestMode: Boolean) {

  val storage: UserStorage = InMemoryUserStorage()
  val imageStorage: ImageStorage = InMemoryImageStorage(issuer + "api/get_image?path=")
//  val imageStorage: ImageStorage = DBMaster.imagesStorage
//  val storage = DBMaster
  val signsMap: HashMap<String, Pair<String, String>> = HashMap()
  val passwordStorage: PasswordStorage = DBMaster.passwordStorage
  val messageStorage: MessageStorage = InMemoryMessageStorage(storage)

  routing {
    route("/") {
      static {
        files("client")
      }

      get {
        call.respondRedirect("/login")
      }

      route("login") {
        static {
          default("client/login.html")
        }
      }

      route("register") {
        static {
          default("client/register.html")
        }
      }

      route("user_info") {
        static {
          default("client/user_info.html")
        }
      }

      route("edit") {
        static {
          default("client/edit.html")
        }
      }

      route("api") {
        route("authenticate") {
          post {
            val credentials = call.receive<Credentials>()

            when (passwordStorage.checkCredentials(credentials)) {
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

            passwordStorage.storeCredentials(Credentials(request.username, request.password))

            storage.putUser(
              request.username,
              UserObject(request.username, request.display_name ?: request.username,
                emptyList(), "kolobok umer")
            )

            val token = JWT.create()
              .withAudience(audience)
              .withIssuer(issuer)
              .withClaim("username", request.username)
              .withExpiresAt(Date(System.currentTimeMillis() + 60000))
              .sign(Algorithm.HMAC256(secret))
            call.respond(TokenResponse(token))
          }
        }

        route("get_image") {
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

        val authenticateIfNeeded = if (!isTestMode) { str: String, body: Route.() -> Unit ->
          authenticate(str) { body() }
        } else { _, body -> body() }

        authenticateIfNeeded("auth-jwt") {
          val getUserId: PipelineContext<Unit, ApplicationCall>.() -> Int = lambda@{
            val principal = call.principal<JWTPrincipal>() ?: if (isTestMode) return@lambda 0 else error("unauthorized")
            val username = principal.payload.getClaim("username").asString()
            val id = storage.getUserId(username) ?: error("no user with specified id found")
            id
          }

          route("settings") {
            post {
              val id = getUserId()
              val info = storage.getUserById(id) ?: error("user info not found")
              val request = call.receive<SettingsRequest>()
              val photoUrls = info.photoUrls.toMutableList()
              if (request.set_photo != null) {
                photoUrls.add(imageStorage.putImage(info.username, Base64.getDecoder().decode(request.set_photo)))
              }
              if (request.delete_photo != null) {
                imageStorage.deleteImage(request.delete_photo)
                photoUrls.filter {url -> url != request.delete_photo}
              }
              var anecdote = info.anecdote
              if (request.anecdote != null)
                anecdote = request.anecdote
              val newInfo = UserObject(info.username, request.display_name ?: info.displayName,
                photoUrls, anecdote)
              storage.updateUser(id, newInfo)
              call.respond(HttpStatusCode.OK)
            }
          }

          get("user_info") {
            val id = call.request.queryParameters["id"]?.let { storage.getUserId(it) } ?: getUserId()
            val info = storage.getUserById(id) ?: error("user info not found")
            call.respond(
              UserObject(
                info.username, info.displayName,
                info.photoUrls.map { url -> imageStorage.getLink(url) ?: ""}.filter { url -> url != "" },
                info.anecdote
              )
            )
          }

          get("chats") {
            val id = getUserId()
            val chats = messageStorage.getChatsById(id)
            call.respond(ChatsResponse(chats.map { storage.getUserById(it) ?: error("lol kek") }))
          }

          get("get_chat") {
            val id = call.request.queryParameters["id"]?.let {
              storage.getUserId(it) ?: error("incorrect user id supplied")
            } ?: error("no user id supplied")
            val myId = getUserId()
            val messages = messageStorage.getMessagesForChat(ChatId(myId, id)) ?: emptyList()
            call.respond(MessagesResponse(messages))
          }

          post("send_message") {
            val messageRequest = call.receive<SendMessageRequest>()
            val id = getUserId()
            messageStorage.sendMessage(id, messageRequest)
          }

          route("match") {
            get {
              val id = getUserId()
              val info = storage.getUserById(id) ?: error("user info not found")
              val nextUser = storage.getNextMatch(id) ?: error("next user not found")
              val sign = "${info.username}to${nextUser.username}" // TODO: add generation of sign
              signsMap[sign] = Pair(info.username, nextUser.username)
              call.respond(MatchResponse(nextUser, sign))
            }

          }

          route("vote") {
            post {
              val id = getUserId()
              val request = call.receive<VoteRequest>()
              if (!signsMap.containsKey(request.sign))
                error("wrong sign")
              val userId = storage.getUserId(request.user_id) ?: error("wrong user id")
              if (request.action == "match")
                storage.addLike(id, userId)
              else
                storage.addUnlike(id, userId)
              call.respond(HttpStatusCode.OK)
            }
          }
        }
      }
    }
  }
}
