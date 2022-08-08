package cz.majksa.mailu.models

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class GeneratePassword(val password: String)
@Serializable
data class GeneratePasswordResult(val data: String)
@Serializable
data class VerifyPassword(val password: String, val hash: String)
@Serializable
data class VerifyPasswordResult(val data: Boolean)
@Serializable
data class ErrorResult(val error: String)

class Security(private val client: HttpClient) {
    private val baseUri: String = "https://pw-gen-api.herokuapp.com"

    suspend fun generatePassword(password: String): String {
        val response: HttpResponse = client.post("$baseUri/generate") {
            contentType(ContentType.Application.Json)
            setBody(GeneratePassword(password))
        }
        if (response.status.isSuccess()) {
            val result: GeneratePasswordResult = response.body()
            return result.data
        }
        val result: ErrorResult = response.body()
        throw Exception(result.error)
    }

    suspend fun verifyPassword(password: String, hash: String): Boolean {
        val response: HttpResponse = client.post("$baseUri/verify") {
            contentType(ContentType.Application.Json)
            setBody(VerifyPassword(password, hash))
        }
        if (response.status.isSuccess()) {
            val result: VerifyPasswordResult = response.body()
            return result.data
        }

        return false
    }
}