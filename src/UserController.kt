package de.novatec

import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

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

    fun delete(id: String) = transaction { Users.deleteWhere { Users.id eq id } }

    fun insert(user: User): ArrayList<User> {
        val id = transaction {
            Users.insert {
                it[Users.adult] = user.adult
                it[Users.avatar] = user.avatar.toString()
                it[Users.name] = user.name
                it[Users.gender] = user.gender.toString()
                it[Users.id] = user.id
            }.generatedKey
        }
        return getAll()
    }
}