package cz.majksa.mailu.dao

import cz.majksa.mailu.models.Aliases
import cz.majksa.mailu.models.Domains
import cz.majksa.mailu.models.Users
import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment) {
        if (environment.developmentMode) {
            initLocal()
        } else {
            initProd(environment.config.config("ktor.database"))
        }
    }

    private fun initLocal() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./data/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(Aliases)
            SchemaUtils.create(Domains)
            SchemaUtils.create(Users)
        }
    }

    private fun initProd(config: ApplicationConfig) {
        val driverClassName = "org.mariadb.jdbc.Driver"
        val jdbcURL = "jdbc:mariadb://${config.property("host").getString()}/${config.property("name").getString()}"
        Database.connect(jdbcURL, driverClassName, config.property("user").getString(), config.property("password").getString())
    }


    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) {
            block()
        }
}