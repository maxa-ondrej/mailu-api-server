package cz.majksa.mailu.plugins

import cz.majksa.mailu.api.getS
import cz.majksa.mailu.routing.domain
import cz.majksa.mailu.routing.login
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.routing.*

fun Application.configureRouting(config: ApplicationConfig) {
    routing {
        route("/") {
            getS {
                "Welcome to Mailu API"
            }
        }
        route("/ping") {
            getS {
                "pong"
            }
        }
        login(config.config("ktor.auth"))
        authenticate("auth-jwt") {
            domain()
        }
    }
}
