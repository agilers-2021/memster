package com.example

import com.example.models.UserObject

interface UserStorage {

  fun getUserCount(): Int

  fun getUserId(username: String): Int?

  fun updateUser(id: Int, user: UserObject): Boolean

  fun putUser(username: String, user: UserObject): Int?

  fun getUserById(id: Int): UserObject?

  fun getNextMatch(id: Int): UserObject?

  fun addMatch(user1: Int, user2: Int)

  fun addUnlike(user1: Int, user2: Int)

  fun addLike(user1: Int, user2: Int)

  fun getChatIds(id: Int): List<Int>
}