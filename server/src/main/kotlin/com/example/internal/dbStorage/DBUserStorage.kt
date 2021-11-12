package com.example.internal.dbStorage

import org.jetbrains.exposed.sql.*
import com.example.UserStorage
import com.example.models.UserObject
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Integer.min
import java.lang.Math.max

class DBUserStorage(val connection: Database): UserStorage {

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
    transaction(connection) {
      SchemaUtils.create(UserTable)
      SchemaUtils.create(Reactions)
      SchemaUtils.create(Matches)
      nextId = UserTable.selectAll().toList().size + 1
    }
  }

  override fun getUserId(username: String): Int? {
    return transaction(connection) {
      UserTable.select { UserTable.username eq username }.singleOrNull()?.let {
        return@transaction it[UserTable.id]
      }
      return@transaction null
    }
  }

  override fun updateUser(id: Int, user: UserObject): Boolean {
    return transaction(connection) {
      UserTable.update({ UserTable.id eq id }) {
        it[username] = user.username
        it[displayName] = user.displayName
        if (user.photoUrls.isEmpty()) {
          it[photoUrls] = null
        } else {
          it[photoUrls] = user.photoUrls.joinToString(separator = " ")
        }
        it[anecdote] = user.anecdote
      }
      return@transaction true
    }
  }

  override fun putUser(username: String, user: UserObject): Int {
    return transaction(connection) {
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
      return@transaction nextId - 1
    }
  }

  override fun getUserById(id: Int): UserObject? {
    return transaction(connection) {
      UserTable.select { UserTable.id eq id }.singleOrNull()?.let {
        return@transaction UserObject(
          it[UserTable.username], it[UserTable.displayName], it[UserTable.photoUrls]?.split(" ") ?: run { emptyList() },
          it[UserTable.anecdote]
        )
      }
      return@transaction null
    }
  }

  override fun getNextMatch(id: Int): UserObject? {
    return transaction(connection) {
      val allReactions = Reactions.select { Reactions.activeId eq id }.map { it[Reactions.passiveId] }.toList()
      UserTable.selectAll().firstOrNull { it[UserTable.id] != id && !allReactions.contains(it[UserTable.id]) }?.let {
        return@transaction UserObject(
          it[UserTable.username], it[UserTable.displayName], it[UserTable.photoUrls]?.split(" ") ?: run { emptyList() },
          it[UserTable.anecdote]
        )
      }
      return@transaction null
    }
  }

  override fun addMatch(user1: Int, user2: Int) {
    transaction(connection) {
      Matches.insert {
        it[firstId] = min(user1, user2)
        it[secondId] = max(user1, user2)
      }
    }
  }

  override fun addUnlike(user1: Int, user2: Int) {
    transaction(connection) {
      Reactions.insert {
        it[activeId] = user1
        it[passiveId] = user2
        it[isLike] = false
      }
    }
  }

  override fun addLike(user1: Int, user2: Int) {
    transaction(connection) {
      Reactions.insert {
        it[activeId] = user1
        it[passiveId] = user2
        it[isLike] = true
      }
      Reactions.select {
        (Reactions.passiveId eq user1) and (Reactions.activeId eq user1) and
                Reactions.isLike
      }.singleOrNull()?.let {
        addMatch(user1, user2)
      }
    }
  }

  override fun getChatIds(id: Int): List<Int> {
    return transaction(connection) {
      val activeLikes =
        Reactions.select { Reactions.activeId eq id }.filter { it[Reactions.isLike] }.map { it[Reactions.passiveId] }
          .toSet()
      val passiveLikes =
        Reactions.select { Reactions.passiveId eq id }.filter { it[Reactions.isLike] }.map { it[Reactions.activeId] }
          .toSet()
      return@transaction (activeLikes intersect passiveLikes).sorted()
    }
  }
}