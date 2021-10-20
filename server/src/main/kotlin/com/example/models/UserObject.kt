package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserObject(
  @SerialName("username") val username: String,
  @SerialName("display_name") val displayName: String,
  @SerialName("current_photo_url") val photoUrl: String? = null,
  @SerialName("anecdote") val anecdote: String
)

@Serializable
data class ErrorDescription(val description: String)

@Serializable
data class Credentials(val username: String, val password: String)

@Serializable
data class TokenResponse(val token: String)

@Serializable
data class RegisterRequest(val username: String, val password: String, val display_name: String? = null)

@Serializable
data class SettingsRequest(val display_name: String? = null, val password: String? = null,
                           val set_photo: String? = null, val delete_photo: Boolean? = null,
                           val anecdote: String? = null)

@Serializable
data class MatchResponse(val user: UserObject, val sign: String)

@Serializable
data class VoteRequest(val user_id: String, val sign: String, val action: String)