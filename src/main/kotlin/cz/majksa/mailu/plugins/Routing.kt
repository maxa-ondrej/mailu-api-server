package cz.majksa.mailu.plugins

import cz.majksa.mailu.routing.domain
import cz.majksa.mailu.routing.login
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.routing.*

fun Application.configureRouting(config: ApplicationConfig) {
    routing {
        login(config.config("ktor.auth"))
        authenticate("auth-jwt") {
            domain()
        }
    }
}
