package com.example.internal.dummyRealization

import com.example.ImageStorage

class InMemoryImageStorage(private val handlerUrl: String = "") : ImageStorage {
    private val storage = HashMap<String, ByteArray>()
    private val imageCounts = HashMap<String, Int>()

//    override fun putImage(username: String, image: ByteArray): String {
//        val id = imageCounts[username] ?: 0
//        val path = "$username$id"
//        storage[path] = image
//        imageCounts[username] = id + 1
//        return path
//    }

    override fun putImage(image: ByteArray): Int {
        TODO("Not yet implemented")
    }

    override fun getImage(id: Int): ByteArray? {
        TODO("Not yet implemented")
    }

//    override fun getImage(path: String): ByteArray? {
//        return storage[path]
//    }

    override fun getLink(path: String?): String? {
        return if (path != null) handlerUrl + path else null
    }

    override fun deleteImage(path: String) {
        storage.remove(path)
    }
}