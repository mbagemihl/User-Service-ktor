package de.novatec

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.assertj.core.api.Assertions.assertThat
import java.util.*
import kotlin.test.Test

class ApplicationIntegrationTest {
    @Test
    fun testPostValidUser() {
        withTestApplication( { module(testing=true) }) {

            val id: UUID

            handleRequest ( HttpMethod.Get, "/users" ).apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                val userList:List<User> = jacksonObjectMapper().readValue<ArrayList<User>>(response.content!!)
                    assertThat(userList).extracting("name").doesNotContain("SUPER UNIQUE USER NAME FTW")
            }

            handleRequest(HttpMethod.Post, "/users") {
                addHeader("content-type", ContentType.Application.Json.toString())
                setBody("{\n" +
                        "        \"name\": \"SUPER UNIQUE USER NAME FTW\",\n" +
                        "        \"gender\": \"MALE\",\n" +
                        "        \"adult\": true,\n" +
                        "        \"avatar\": \"MARIO\"\n" +
                        "    }")
            }

            handleRequest (HttpMethod.Get, "/users").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                val userList:List<User> = jacksonObjectMapper().readValue<ArrayList<User>>(response.content!!)
                assertThat(userList).extracting("name").contains("SUPER UNIQUE USER NAME FTW")
                id = userList.filter { it.name == "SUPER UNIQUE USER NAME FTW" }[0].id
            }

            handleRequest ( HttpMethod.Delete, "/users/$id").apply {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                val userList:List<User> = jacksonObjectMapper().readValue<ArrayList<User>>(response.content!!)
                assertThat(userList).extracting("name").doesNotContain("SUPER UNIQUE USER NAME FTW")
            }
        }
    }
}

data class User(
    val id: UUID,
    val name: String,
    val gender: Gender,
    val adult: Boolean,
    val avatar: Avatar
)
