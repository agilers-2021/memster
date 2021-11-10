package com.example.internal.dbStorage

import org.jetbrains.exposed.sql.*
import com.example.UserStorage
import com.example.models.UserObject
import java.lang.Integer.min
import java.lang.Math.max

object DBUserStorage: UserStorage {

  private var nextId = 1

  object UserTable : Table() {
    val id = integer("id").primaryKey()
    val username = varchar("username", length=100)
    val displayName = varchar("display_name", length=100)
    val photoUrls = varchar("photoUrl", length=100).nullable()
    val anecdote = text("anecdote")
  }

  object Reactions: Table() {
    val activeId =  integer("activeId").primaryKey()
    val passiveId = integer("passiveId").primaryKey()
    val isLike = bool("isLike")
  }


  object Matches: Table() {
    val firstId =  integer("firstId").primaryKey()
    val secondId = integer("secondId").primaryKey()
  }

  fun init() {
    SchemaUtils.create(UserTable)
    SchemaUtils.create(Reactions)
    SchemaUtils.create(Matches)
    nextId = UserTable.selectAll().toList().size + 1
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
      if (user.photoUrls.isEmpty()) {
        it[photoUrls] = null
      } else {
        it[photoUrls] = user.photoUrls.joinToString(separator = " ")
      }
      it[anecdote] = user.anecdote
    }
    return true
  }

  override fun putUser(username: String, user: UserObject): Int {
    UserTable.insert {
      it[id] = nextId
      nextId += 1
      it[UserTable.username] = username
      it[displayName] = user.displayName
      if (user.photoUrls.isEmpty()) {
        it[photoUrls] = null
      } else {
        it[photoUrls] = user.photoUrls.joinToString(separator = " ")
      }
      it[anecdote] = user.anecdote
    }
    return nextId - 1
  }

  override fun getUserById(id: Int): UserObject? {
    UserTable.select {UserTable.id eq id}.singleOrNull()?.let {
      return UserObject(it[UserTable.username], it[UserTable.displayName], it[UserTable.photoUrls]?.split(" ") ?: run { emptyList()},
      it[UserTable.anecdote])
    }
    return null
  }

  override fun getNextMatch(id: Int): UserObject? {
    val allReactions = Reactions.select { Reactions.activeId eq id}.map { it[Reactions.passiveId] }.toList()
    UserTable.selectAll().firstOrNull { it[UserTable.id] != id && !allReactions.contains(it[UserTable.id]) }?.let {
      return@getNextMatch UserObject(it[UserTable.username], it[UserTable.displayName], it[UserTable.photoUrls]?.split(" ") ?: run { emptyList()},
        it[UserTable.anecdote])
    }
    return null
  }

  override fun addMatch(user1: Int, user2: Int) {
    Matches.insert {
      it[firstId] = min(user1, user2)
      it[secondId] = max(user1, user2)
    }
  }

  override fun addUnlike(user1: Int, user2: Int) {
    Reactions.insert {
      it[activeId] = user1
      it[passiveId] = user2
      it[isLike] = false
    }
  }

  override fun addLike(user1: Int, user2: Int) {
    Reactions.insert {
      it[activeId] = user1
      it[passiveId] = user2
      it[isLike] = true
    }
    Reactions.select {
      (Reactions.passiveId  eq user1) and (Reactions.activeId eq user1) and
              Reactions.isLike}.singleOrNull()?.let {
      addMatch(user1, user2)
    }
  }

  override fun getChatIds(id: Int): List<Int> {
    val activeLikes = Reactions.select { Reactions.activeId eq id }.filter { it[Reactions.isLike] }.map { it[Reactions.passiveId] }.toSet()
    val passiveLikes = Reactions.select { Reactions.passiveId eq id }.filter { it[Reactions.isLike] }.map { it[Reactions.activeId] }.toSet()
    return (activeLikes intersect passiveLikes).sorted()
  }
}