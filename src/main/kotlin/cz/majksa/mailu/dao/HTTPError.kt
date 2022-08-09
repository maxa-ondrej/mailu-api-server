package cz.majksa.mailu.dao

import io.ktor.http.*

sealed class HTTPError(override val message: String, val code: HttpStatusCode) : Exception(message) {
}

class BadRequestError(message: String): HTTPError(message, HttpStatusCode.BadRequest)

class NotFoundError(subject: String, query: String): HTTPError("$subject not found by $query", HttpStatusCode.NotFound)

class ConflictError(message: String? = null): HTTPError(message ?: "SQL constrain violated", HttpStatusCode.Conflict)