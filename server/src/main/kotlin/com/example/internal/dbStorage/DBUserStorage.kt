package com.example.internal.dbStorage

import org.jetbrains.exposed.sql.*
import com.example.UserStorage
import com.example.models.UserObject


class DBUserStorage: UserStorage {

  private var nextId = 1

  object UserTable : Table() {
    val id = integer("id").primaryKey()
    val name = varchar("username", length=100)
    val displayname = varchar("displayname", length=100)
    val photoUrl = varchar("photoUrl", length=100).nullable()
  }

  fun init() {
    SchemaUtils.create(UserTable)
    nextId = UserTable.selectAll().toList().size + 1
  }

  override fun getUserId(username: String): Int? {
    UserTable.select {UserTable.name eq username}.singleOrNull()?.let {
      return it[UserTable.id]
    }
    return null
  }

  override fun updateUser(id: Int, user: UserObject): Boolean {
    TODO("Not yet implemented")
  }

  override fun putUser(username: String, userObject: UserObject): Int? {
    UserTable.insert {
      it[id] = nextId
      nextId += 1
      it[name] = username
      it[photoUrl] = userObject.photoUrl
    }
    return nextId - 1
  }

  override fun getUserById(id: Int): UserObject? {
    UserTable.select {UserTable.id eq id}.singleOrNull()?.let {
      return UserObject(it[UserTable.name], it[UserTable.displayname], it[UserTable.photoUrl])
    }
    return null
  }

}