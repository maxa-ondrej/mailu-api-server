package cz.majksa.mailu.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Domain(
    val name: String,
    val maxUsers: Int,
    val maxAliases: Int,
    val maxAllocatedStorage: Long,
    val signupEnabled: Boolean
)

@Serializable
data class CreateDomain(val name: String)

object Domains : Table("domain") {
    val name = varchar("name", 80)
    val maxUsers = integer("max_users").default(-1)
    val maxAliases = integer("max_aliases").default(-1)
    val maxAllocatedStorage = long("max_quota_bytes").default(0)
    val signupEnabled = bool("signup_enabled").default(false)

    override val primaryKey = PrimaryKey(name)
}

fun resultRowToDomain(row: ResultRow) = Domain(
    name = row[Domains.name],
    maxUsers = row[Domains.maxUsers],
    maxAliases = row[Domains.maxAliases],
    maxAllocatedStorage = row[Domains.maxAllocatedStorage],
    signupEnabled = row[Domains.signupEnabled],
)