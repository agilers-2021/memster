package com.example.internal.dummyRealization

import com.example.UserStorage
import com.example.models.UserObject

class InMemoryUserStorage: UserStorage {
  private val usernameToId = hashMapOf<String, Int>()
  private val storage = hashMapOf<Int, UserObject>()

  private var nextId = 0

  override fun getUserId(username: String): Int? {
    return usernameToId[username]
  }

  override fun updateUser(id: Int, user: UserObject): Boolean {
    storage[id] = user
    return true
  }

  override fun putUser(username: String, userObject: UserObject): Int {
    val id = nextId++
    usernameToId[username] = id
    storage[id] = userObject
    return id
  }

  override fun getUserById(id: Int): UserObject? {
    return storage[id]
  }
}