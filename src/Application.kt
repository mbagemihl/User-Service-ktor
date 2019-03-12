package de.novatec

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.PipelineContext

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val userlist: ArrayList<User> = ArrayList()
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(CORS) {
        anyHost() // #yolo
    }

    routing {
        post("/users") {
            val userObject = call.receive<User>()
            userlist += userObject
            call.respond(userlist)
        }

        get ("/users") {
            call.respond(userlist)
        }

    }
}

data class User(
    val name: String,
    val gender: Gender,
    val adult: Boolean,
    val avatar: Avatar
)

enum class Gender { MALE, FEMALE, NONE }

enum class Avatar{OCTOCAT, MARIO, ASH, POKEBALL, BULBASAUR, CHARMANDER, SQUIRTLE, KIRBY}

private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend  () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText("""{"error":"$e"}""", ContentType.parse("application/json"), HttpStatusCode.InternalServerError)
        null
    }
}

private suspend fun ApplicationCall.respondSuccessJson(value: Boolean = true) = respond("""{"success":"$value"}""")