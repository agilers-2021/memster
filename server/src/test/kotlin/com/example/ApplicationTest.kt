package com.example

import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
  @Test
  fun testRoot() {
    withTestApplication({
      (environment.config as MapApplicationConfig).apply {
        put("jwt.secret", "secret")
        put("jwt.issuer", "http://0.0.0.0:8080/")
        put("jwt.audience", "http://0.0.0.0:8080/hello")
        put("jwt.realm", "Access to 'hello'")
      }
      module()
    }) {
      handleRequest(HttpMethod.Get, "/").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("Hi, I'm Memster!", response.content)
      }
    }
  }
}