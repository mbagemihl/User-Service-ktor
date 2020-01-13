package de.novatec

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import com.fasterxml.jackson.databind.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.engine.apache.Apache


import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val loginProviders = listOf(
    OAuthServerSettings.OAuth2ServerSettings(
        name = "github",
        authorizeUrl = "https://github.com/login/oauth/authorize",
        accessTokenUrl = "https://github.com/login/oauth/access_token",
        clientId = "63e9a2d434a15bff8a3e",
        clientSecret = "9944bbfe361449ee2d5f7b6966bf33a53c1e986c"
    )
).associateBy { it.name }

@KtorExperimentalLocationsAPI
@Location("/login/{type?}") class login(val type: String = "github")

@KtorExperimentalLocationsAPI
@kotlin.jvm.JvmOverloads
@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {

    fun initDB() {
        val config = HikariConfig("/hikari.properties")
        config.schema = "public"
        val ds = HikariDataSource(config)
        Database.connect(ds)

        transaction {
            SchemaUtils.create(Users)
        }
    }

    install(Locations)
    install(Authentication) {
        oauth("gitHubOAuth") {
            client = HttpClient(Apache)
            providerLookup = { loginProviders[application.locations.resolve<login>(login::class, this).type] }
            urlProvider = { url(login(it.name)) }
        }

        basic(name = "password") {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == "marcel" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

        basic(name = "apiKey") {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == "secretApiKey") {
                    UserIdPrincipal("anonymous")
                } else {
                    null
                }

            }
        }
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