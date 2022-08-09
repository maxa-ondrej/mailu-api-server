package cz.majksa.mailu.api

import cz.majksa.mailu.dao.HTTPError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable

@Serializable
data class Error(val code: Int, val description: String, val message: String)

@Serializable
data class StringResponse(val data: String)

@Serializable
data class IntResponse(val data: Int)

@Serializable
data class BooleanResponse(val data: Boolean)