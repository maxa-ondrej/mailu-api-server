package cz.majksa.mailu.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Alias(
    val email: String,
    val destination: String,
    val localPart: String,
    val domainName: String,
    val wildcard: Boolean
)

@Serializable
data class CreateAlias(
    val name: String,
    val wildcard: Boolean
)

object Aliases : Table("alias") {
    /**
     * Important user data
     */
    val email = varchar("email", 255)
    val destination = varchar("destination", 1023)
    val localPart = varchar("localpart", 80)
    val domainName = varchar("domain_name", 80)
    val wildcard = bool("wildcard").default(true)

    override val primaryKey = PrimaryKey(Users.email)
}

fun resultRowToAlias(row: ResultRow) = Alias(
    email = row[Aliases.email],
    destination = row[Aliases.destination],
    domainName = row[Aliases.domainName],
    localPart = row[Aliases.localPart],
    wildcard = row[Aliases.wildcard]
)