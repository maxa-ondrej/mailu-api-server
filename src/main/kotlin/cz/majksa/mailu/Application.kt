package cz.majksa.mailu

import cz.majksa.mailu.dao.DatabaseFactory
import cz.majksa.mailu.plugins.configureRouting
import cz.majksa.mailu.plugins.configureSecurity
import cz.majksa.mailu.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment)
    configureSecurity()
    configureSerialization()
    configureRouting(environment.config)
}