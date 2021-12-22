package com.example

import kotlinx.serialization.Serializable

@Serializable
data class ImagesPutRequest(val new_photos: List<String> = emptyList())
@Serializable
data class ImagesDeleteRequest(val delete_photos: List<Int> = emptyList())

@Serializable
data class ImagesResponse(val photos_ids: List<Int> = emptyList())