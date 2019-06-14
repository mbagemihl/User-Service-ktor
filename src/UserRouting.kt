package de.novatec

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import java.util.*

val userController = UserController()

fun Route.userRoutes() {
    post("/users") {
        val requestUser = call.receive<RequestUser>()
        call.respond(
            userController.insert(
                User(
                    UUID.randomUUID(),
                    requestUser.name,
                    requestUser.gender,
                    requestUser.adult,
                    requestUser.avatar
                )
            )
        )
    }

    get("/users") {
        call.respond(userController.getAll())
    }

    delete("/users/{id}") {
        UserController().delete(call.parameters["id"]!!)
        call.respond(userController.getAll())
    }
}