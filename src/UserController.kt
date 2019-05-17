package de.novatec

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
}