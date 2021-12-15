package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.audience
import com.example.issuer
import com.example.myRealm
import com.example.secret
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.application.*
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .acceptExpiresAt(604800L) // a week
                    .build())

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
