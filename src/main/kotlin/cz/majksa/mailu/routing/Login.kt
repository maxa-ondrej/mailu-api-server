package cz.majksa.mailu.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cz.majksa.mailu.api.error
import cz.majksa.mailu.api.success
import cz.majksa.mailu.plugins.LoginUser
import cz.majksa.mailu.plugins.secret
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.routing.*


fun Route.login(config: ApplicationConfig) {
    val username = config.property("username").getString()
    val password = config.property("password").getString()

    post("/login") {
        val user = call.receive<LoginUser>()
        if (user.username != username) {
            return@post call.error(HttpStatusCode.Unauthorized, "Invalid username")
        }
        if (user.password != password) {
            return@post call.error(HttpStatusCode.Unauthorized, "Invalid password")
        }
        val token = JWT.create()
            .withClaim("username", user.username)
            .sign(Algorithm.HMAC256(secret))
        call.success(hashMapOf("token" to token))
    }
}
