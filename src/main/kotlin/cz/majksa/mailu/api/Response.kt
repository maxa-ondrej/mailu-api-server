package cz.majksa.mailu.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class Error(val code: Int, val description: String, val message: String)

suspend fun error(call: ApplicationCall, status: HttpStatusCode, message: String) {
    call.respond(status, Error(status.value, status.description, message))
}

suspend fun success(call: ApplicationCall, data: Any, status: HttpStatusCode = HttpStatusCode.OK) {
    call.respond(status, data)
}