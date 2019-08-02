package de.novatec

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.html.respondHtml
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.location
import io.ktor.locations.locations
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import kotlinx.html.*

import java.util.*

val userController = UserController()

@KtorExperimentalLocationsAPI
fun Route.userRoutes() {
    authenticate("password") {
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

        get("/usersBasic") {
            call.respond(userController.getAll())
        }
    }

    get("/users") {
        call.respond(userController.getAll())
    }

    authenticate("gitHubOAuth") {
        location<login>() {
            println("GOT HERE!")
            param("error") {
                handle {
                    call.loginFailedPage(call.parameters.getAll("error").orEmpty())
                }
            }
            handle {
                val principal = call.authentication.principal<OAuthAccessTokenResponse>()
                if (principal != null) {
                    call.loggedInSuccessResponse(principal)
                } else {
                    call.loginPage()
                }
            }
        }
        get("/usersOAuth") {
            call.respond(userController.getAll())
        }
        delete("/users/{id}") {
            UserController().delete(call.parameters["id"]!!)
            call.respond(userController.getAll())
        }
    }
}

@KtorExperimentalLocationsAPI
private fun <T : Any> ApplicationCall.redirectUrl(t: T, secure: Boolean = true): String {
    val hostPort = request.host() + request.port().let { port -> if (port == 80) "" else ":$port" }
    val protocol = when {
        secure -> "https"
        else -> "http"
    }
    return "$protocol://$hostPort${application.locations.href(t)}"
}

@KtorExperimentalLocationsAPI
private suspend fun ApplicationCall.loginPage() {
    respondHtml {
        head {
            title { +"Login with" }
        }
        body {
            h1 {
                +"Login with:"
            }

            for (p in loginProviders) {
                p {
                    a(href = application.locations.href(login(p.key))) {
                        +p.key
                    }
                }
            }
        }
    }
}

private suspend fun ApplicationCall.loginFailedPage(errors: List<String>) {
    respondHtml {
        head {
            title { +"Login with" }
        }
        body {
            h1 {
                +"Login error"
            }

            for (e in errors) {
                p {
                    +e
                }
            }
        }
    }
}

private suspend fun ApplicationCall.loggedInSuccessResponse(callback: OAuthAccessTokenResponse) {
    respondHtml {
        head {
            title { +"Logged in" }
        }
        body {
            h1 {
                +"You are logged in"
            }
            p {
                +"Your token is $callback"
            }
        }
    }
}