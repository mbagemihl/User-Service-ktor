package de.novatec

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.Database
import java.util.*
import kotlin.collections.HashSet



fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    fun initDB() {
        val config = HikariConfig("/hikari.properties")
        config.schema = "userSchema"
        val ds = HikariDataSource(config)
        Database.connect(ds)
    }

    val userSet: HashSet<User> = HashSet()
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(CORS) {
        anyHost() // #yolo
        method(HttpMethod.Delete)
    }
    initDB()
    routing {
        post("/users") {
            val requestUser = call.receive<RequestUser>()
            val userObject = User(UUID.randomUUID(),requestUser.name,requestUser.gender,requestUser.adult,requestUser.avatar)

            userSet.add(userObject)
            call.respond(userSet)
        }

        get ("/users") {
            call.respond(UserController().getAll())
        }

        delete("/users/{id}") {
            val userId = call.parameters["id"]
            val user = userSet.find { it.id.toString() == userId }
            userSet.drop(userSet.indexOf(user))
            call.respond(userSet)
        }
    }
}


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