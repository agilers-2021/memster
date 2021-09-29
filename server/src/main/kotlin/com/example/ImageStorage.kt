package com.example

interface ImageStorage {
    fun putImage(username: String, image: ByteArray): String

    fun getImage(path: String): ByteArray?

    fun deleteImage(path: String)
}