package com.example.plugins

import com.example.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import java.util.*

fun Application.configureRouting() {

    val imageStorage: ImageStorage = DBMaster.imagesStorage

    routing {
        route("/") {
            route("get_image") {
                get {
                    val path = call.parameters["id"]
                    if (path == null) {
                        call.respond(HttpStatusCode.BadRequest, "Wrong path")
                        return@get
                    }
                    val img = imageStorage.getImage(path.toInt())
                    if (img == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    call.respondBytes(img)
                }
            }
            route("put_images") {
                post {
                    val request = call.receive<ImagesPutRequest>()
                    val r = request.new_photos.map {
                        imageStorage.putImage(Base64.getDecoder().decode(it))
                    }
                    call.respond(ImagesResponse(r))
                }
            }
            route("delete_images") {
                post {
                    val request = call.receive<ImagesDeleteRequest>()
                    request.delete_photos.forEach {
                        imageStorage.deleteImage(it)
                    }
                }
            }
        }
    }
}
