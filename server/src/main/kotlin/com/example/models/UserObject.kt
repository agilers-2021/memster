package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserObject(
  @SerialName("username") val username: String,
  @SerialName("display_name") val displayName: String,
  @SerialName("photo_urls") val photoUrls: List<String> = emptyList(),
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
                           val set_photo: String? = null, val delete_photo: String? = null,
                           val anecdote: String? = null)

@Serializable
data class MatchResponse(val user: UserObject, val sign: String)

@Serializable
data class VoteRequest(val sign: String, val action: String)


// I assume we pass the image in the format we do in settings, i.e. string. @Anton Paramonov
@Serializable
data class SendMessageRequest(
  val receiver: String, val text: String, val image: String? = null
)

@Serializable
data class ChatMessage(
  val id: Int, val text: String, val datetime: String, val sender: Int, val receiver: Int, val imageId: Int? = null
)

@Serializable
data class MessagesResponse(
  val messages: List<ChatMessage>
)

@Serializable
data class ChatsResponse(
  val users: List<UserObject>
)

