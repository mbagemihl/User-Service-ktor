package de.novatec

import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.jackson.*
import io.ktor.features.*
import kotlin.test.*
import io.ktor.server.testing.*
import java.util.*

class ApplicationTest {
    @Test
    fun testGetEmptyUserList() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/users").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("[ ]", response.content)
            }
        }
    }

    @Test
    fun testPostValidUser() {
        withTestApplication( { module(testing=true) }) {
            handleRequest(HttpMethod.Post, "/users") {
                addHeader("content-type", ContentType.Application.Json.toString())
                setBody("{\n" +
                        "        \"name\": \"Super Mario\",\n" +
                        "        \"gender\": \"MALE\",\n" +
                        "        \"adult\": true,\n" +
                        "        \"avatar\": \"MARIO\"\n" +
                        "    }")
            }

            handleRequest (HttpMethod.Get, "/users").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val userList:List<User> = jacksonObjectMapper().readValue<ArrayList<User>>(response.content!!)
                assertEquals(userList.size,1)
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
