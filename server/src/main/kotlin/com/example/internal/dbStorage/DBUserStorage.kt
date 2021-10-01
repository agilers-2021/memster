package com.example.internal.dbStorage

import org.jetbrains.exposed.sql.*
import com.example.UserStorage
import com.example.models.UserObject


object DBUserStorage: UserStorage {

  private var nextId = 1

  object UserTable : Table() {
    val id = integer("id").primaryKey()
    val username = varchar("username", length=100)
    val displayName = varchar("display_name", length=100)
    val photoUrl = varchar("photoUrl", length=100).nullable()
  }

  fun init() {
    SchemaUtils.create(UserTable)
    nextId = UserTable.selectAll().toList().size + 1
    println(nextId)
  }

  override fun getUserId(username: String): Int? {
    UserTable.select {UserTable.username eq username}.singleOrNull()?.let {
      return it[UserTable.id]
    }
    return null
  }

  override fun updateUser(id: Int, user: UserObject): Boolean {
    UserTable.update({UserTable.id eq id}) {
      it[username] = user.username
      it[displayName] = user.displayName
      it[photoUrl] = user.photoUrl
    }
    return true
  }

  override fun putUser(username: String, userObject: UserObject): Int? {
    println(nextId)
    UserTable.insert {
      it[id] = nextId
      nextId += 1
      it[UserTable.username] = username
      it[displayName] = userObject.displayName
      it[photoUrl] = userObject.photoUrl
    }
    return nextId - 1
  }

  override fun getUserById(id: Int): UserObject? {
    UserTable.select {UserTable.id eq id}.singleOrNull()?.let {
      return UserObject(it[UserTable.username], it[UserTable.displayName], it[UserTable.photoUrl])
    }
    return null
  }
}