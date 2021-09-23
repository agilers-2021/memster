package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserObject(
  val id: Int,
  @SerialName("display_name") val displayName: String,
  @SerialName("current_photo_url") val photoUrl: String
)

interface Response

@Serializable
data class SuccessResponse<T>(val success: Boolean, val data: T): Response

@Serializable
data class ErrorDescription(val description: String)

@Serializable
data class FailureResponse(val success: Boolean, val error: ErrorDescription): Response

data class Credentials(val username: String, val password: String)

@Serializable
data class TokenResponse(val token: String)
