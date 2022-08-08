package cz.majksa.mailu

import cz.majksa.mailu.dao.DatabaseFactory
import cz.majksa.mailu.models.Security
import cz.majksa.mailu.plugins.configureLoginRouting
import cz.majksa.mailu.plugins.configureRouting
import cz.majksa.mailu.plugins.configureSecurity
import cz.majksa.mailu.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    val security = Security(client)
    DatabaseFactory.init(environment)
    configureSecurity()
    configureSerialization()
    configureLoginRouting(environment.config.config("ktor.auth"))
    configureRouting(security)
}