package com.example.internal.dummyRealization

import com.example.UserStorage
import com.example.models.UserObject

class InMemoryUserStorage: UserStorage {
  private val usernameToId = hashMapOf<String, Int>()
  private val storage = hashMapOf<Int, UserObject>()
  private val matches = hashMapOf<Int, HashSet<Int>>()
  private val mismatches = hashMapOf<Int, HashSet<Int>>()

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

  override fun getNextMatch(id: Int): UserObject? {
    val matchUserId = storage.keys.filter {newId -> newId != id &&
            (mismatches[id]?.contains(newId) ?: true)}.random()
    return storage[matchUserId]
  }

  override fun addMatch(user1: Int, user2: Int) {
    if (!matches.containsKey(user1))
      matches[user1] = HashSet()
    if (!matches.containsKey(user2))
      matches[user2] = HashSet()
    matches[user1]?.add(user2)
    matches[user2]?.add(user1)
  }

  override fun addMismatch(user1: Int, user2: Int) {
    if (!mismatches.containsKey(user1))
      mismatches[user1] = HashSet()
    if (!mismatches.containsKey(user2))
      mismatches[user2] = HashSet()
    mismatches[user1]?.add(user2)
    mismatches[user2]?.add(user1)
  }
}