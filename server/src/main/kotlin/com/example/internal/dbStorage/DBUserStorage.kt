package com.example.internal.dbStorage

import com.example.UserStorage
import com.example.models.UserObject

class DBUserStorage: UserStorage {
  override fun getUserId(username: String): Int? {
    TODO("Not yet implemented")
  }

  override fun updateUser(id: Int, user: UserObject): Boolean {
    TODO("Not yet implemented")
  }

  override fun putUser(username: String, userObject: UserObject): Int? {
    TODO("Not yet implemented")
  }

  override fun getUserById(id: Int): UserObject? {
    TODO("Not yet implemented")
  }
}