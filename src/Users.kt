package de.novatec

import org.jetbrains.exposed.sql.Table
import java.util.*

object Users : Table() {
    val id = uuid("id").primaryKey().autoIncrement()
    val name = text("name")
    val avatar = text("avatar")
    val gender = text("gender")
    val adult = bool("adult")
}


data class User(
    val id: UUID,
    val name: String,
    val gender: Gender,
    val adult: Boolean,
    val avatar: Avatar
)

data class RequestUser(
    val name: String,
    val gender: Gender,
    val adult: Boolean,
    val avatar: Avatar
)