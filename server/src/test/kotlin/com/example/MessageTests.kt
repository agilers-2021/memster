package com.example

import com.example.models.MessagesResponse
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class MessageTests {
  @Test
  fun testRoot() {
    withTestApplication({
      (environment.config as MapApplicationConfig).apply {
        put("jwt.secret", "secret")
        put("jwt.issuer", "http://0.0.0.0:8080/")
        put("jwt.audience", "http://0.0.0.0:8080/hello")
        put("jwt.realm", "Access to 'hello'")
      }
      module(true)
    }) {
      handleRequest(HttpMethod.Get, "api/chats").apply {
        assertEquals("{\"users\":[]}", response.content)
      }
    }
  }
}