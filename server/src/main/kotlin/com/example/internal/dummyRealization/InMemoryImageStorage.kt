package com.example.internal.dummyRealization

import com.example.ImageStorage

class InMemoryImageStorage: ImageStorage {
    private val storage = HashMap<String, ByteArray>()
    private val imageCounts = HashMap<String, Int>()

    override fun putImage(username: String, image: ByteArray): String {
        val id = imageCounts[username] ?: 0
        val path = "$username/$id"
        storage[path] = image
        imageCounts[username] = id + 1
        return path
    }

    override fun getImage(path: String): ByteArray? {
        return storage[path]
    }

    override fun deleteImage(path: String) {
        storage.remove(path)
    }
}