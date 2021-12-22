package com.example.internal.dummyRealization


import com.example.models.ImagesDeleteRequest
import com.example.models.ImagesPutRequest
import com.example.models.ImagesResponse
import io.ktor.client.request.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.statement.*
import io.ktor.http.*


class MicroserviceImagesStorage  {
    val client = HttpClient() {
        install(JsonFeature)
    }


    suspend fun getImage(id: String): ByteArray? {
        val response: HttpStatement = client.request("http://0.0.0.0:8081/get_image") {
            method = HttpMethod.Get
            parameter("id", id)
        }
        return response.receive<ByteArray>()
    }

    suspend fun putImages(l: List<String>) : List<Int> {
        if (l.isEmpty())
            return emptyList()
        val response: HttpStatement = client.request("http://0.0.0.0:8081/put_images") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            body = ImagesPutRequest(l)
        }
        return response.receive<ImagesResponse>().photos_ids
    }

    suspend fun deleteImages(l: List<Int>) {
        val response: HttpStatement = client.request("http://0.0.0.0:8081/delete_images") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            body = ImagesDeleteRequest(l)
        }
    }
}