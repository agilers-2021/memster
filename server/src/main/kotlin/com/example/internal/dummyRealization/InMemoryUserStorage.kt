package com.example.internal.dummyRealization

import com.example.UserStorage
import com.example.models.UserObject

class InMemoryUserStorage: UserStorage {
  private val usernameToId = hashMapOf<String, Int>()
  private val storage = hashMapOf<Int, UserObject>()
  private val matches = hashMapOf<Int, HashSet<Int>>()
  private val unlikes = hashMapOf<Int, HashSet<Int>>()
  private val likes = hashMapOf<Int, HashSet<Int>>()

  private var nextId = 0

  override fun getUserId(username: String): Int? {
    return usernameToId[username]
  }

  override fun updateUser(id: Int, user: UserObject): Boolean {
    storage[id] = user
    return true
  }

  override fun putUser(username: String, user: UserObject): Int {
    val id = nextId++
    usernameToId[username] = id
    storage[id] = user
    return id
  }

  override fun getUserById(id: Int): UserObject? {
    return storage[id]
  }

  override fun getNextMatch(id: Int): UserObject? {
    val matchUserId = storage.keys.filter {newId -> newId != id &&
            !(unlikes[id]?.contains(newId) ?: false && likes[id]?.contains(newId) ?: false)}.random()
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

  override fun addUnlike(user1: Int, user2: Int) {
    if (!unlikes.containsKey(user1))
      unlikes[user1] = HashSet()
    if (!unlikes.containsKey(user2))
      unlikes[user2] = HashSet()
    unlikes[user1]?.add(user2)
    unlikes[user2]?.add(user1)
  }

  override fun addLike(user1: Int, user2: Int) {
    if (!likes.containsKey(user1))
      likes[user1] = HashSet()
    likes[user1]?.add(user2)
    if (likes.containsKey(user2) && likes[user2]?.contains(user1) == true)
      addMatch(user1, user2)
  }
  // FIXME
  override fun getChatIds(id: Int): List<Int> = emptyList()
}