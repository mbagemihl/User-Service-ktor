package de.novatec

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.Database
import kotlin.collections.HashSet


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


@kotlin.jvm.JvmOverloads
@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {

    fun initDB() {
        val config = HikariConfig("/hikari.properties")
        config.schema = "userSchema"
        val ds = HikariDataSource(config)
        Database.connect(ds)
    }

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
    routing { userRoutes() }
}

private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText(
            """{"error":"$e"}""",
            ContentType.parse("application/json"),
            HttpStatusCode.InternalServerError
        )
        null
    }
}

private suspend fun ApplicationCall.respondSuccessJson(value: Boolean = true) = respond("""{"success":"$value"}""")