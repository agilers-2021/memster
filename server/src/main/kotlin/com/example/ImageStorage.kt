package com.example

interface ImageStorage {
    fun putImage(image: ByteArray): Int

    fun getImage(id: Int): ByteArray?

    fun getLink(path: String?): String?

    fun deleteImage(path: String)
}