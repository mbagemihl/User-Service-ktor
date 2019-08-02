package de.novatec

import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.collections.ArrayList

class UserController {

    fun getAll(): ArrayList<User> {
        val users: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                users.add(
                    User(
                        id = it[Users.id],
                        adult = it[Users.adult],
                        avatar = Avatar.valueOf(it[Users.avatar].toUpperCase()),
                        gender = Gender.valueOf(it[Users.gender].toUpperCase()),
                        name = it[Users.name]
                    )
                )
            }
        }
        return users
    }

    fun delete(id: String) = transaction { Users.deleteWhere { Users.id eq UUID.fromString(id) } }

    fun insert(user: User) {
        transaction {
            Users.insert {
                it[adult] = user.adult
                it[avatar] = user.avatar.toString()
                it[name] = user.name
                it[gender] = user.gender.toString()
                it[id] = user.id
            }
        }
    }
}