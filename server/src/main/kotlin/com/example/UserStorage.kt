package com.example

import com.example.models.UserObject

interface UserStorage {

  fun getUserId(username: String): Int?

  fun updateUser(id: Int, user: UserObject): Boolean

  fun putUser(username: String, userObject: UserObject): Int?

  fun getUserById(id: Int): UserObject?
}