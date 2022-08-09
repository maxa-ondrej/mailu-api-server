package cz.majksa.mailu.dao

import cz.majksa.mailu.dao.DatabaseFactory.dbQuery
import cz.majksa.mailu.models.*
import cz.majksa.mailu.models.Alias
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import java.time.LocalDate
import kotlin.jvm.Throws

class FacadeImpl : Facade {

    override suspend fun domains(): List<Domain> = dbQuery {
        Domains.selectAll().map(::resultRowToDomain)
    }

    @Throws(NotFoundError::class)
    override suspend fun domain(name: String): Domain = dbQuery {
        Domains
            .select { Domains.name eq name }
            .map(::resultRowToDomain)
            .singleOrNull() ?: throw NotFoundError("Domain", "name=$name")
    }

    @Throws(ConflictError::class)
    override suspend fun addDomain(name: String): Domain = dbQuery {
        try {
            val insertStatement = Domains.insert {
                it[this.name] = name
                it[createdAt] = LocalDate.now()
            }

            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToDomain) ?: throw ConflictError()
        } catch (e: ExposedSQLException) {
            throw ConflictError(e.cause?.message)
        }
    }

    @Throws(ConflictError::class)
    override suspend fun renameDomain(domain: Domain, name: String): String = dbQuery {
        try {
            Domains
                .update({ Domains.name eq domain.name }) {
                    it[this.name] = name
                }
            name
        } catch (e: ExposedSQLException) {
            throw ConflictError(e.cause?.message)
        }
    }

    override suspend fun deleteDomain(domain: Domain): Boolean = dbQuery {
        Domains.deleteWhere { Domains.name eq domain.name } > 0
    }

    override suspend fun domainUsers(domain: Domain): List<User> = dbQuery {
        Users.select { Users.domainName eq domain.name }.map(::resultRowToUser)
    }

    @Throws(NotFoundError::class)
    override suspend fun user(domain: Domain, name: String): User = dbQuery {
        val email = getEmail(domain, name)
        Users
            .select { Users.email eq email }
            .map(::resultRowToUser)
            .singleOrNull() ?: throw NotFoundError("User", "email=$email")
    }

    @Throws(ConflictError::class)
    override suspend fun addUser(domain: Domain, name: String, displayedName: String, password: String): User =
        dbQuery {
            val hash = Security.generatePassword(password)
            try {
                val insertStatement = Users.insert {
                    it[email] = getEmail(domain, name)
                    it[this.displayedName] = displayedName
                    it[localPart] = name
                    it[domainName] = domain.name
                    it[createdAt] = LocalDate.now()
                    it[this.password] = hash
                }

                insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser) ?: throw ConflictError()
            } catch (e: ExposedSQLException) {
                throw ConflictError(e.cause?.message)
            }
        }

    override suspend fun changeUserPassword(user: User, password: String): Boolean = dbQuery {
        val hash = Security.generatePassword(password)
        Users
            .update({ Users.email eq user.email }) {
                it[this.password] = hash
            } > 0
    }

    override suspend fun renameUser(user: User, name: String): String = dbQuery {
        try {
            Users
                .update({ Users.email eq user.email }) {
                    it[this.localPart] = name
                    it[email] = "$name@${user.domain}"
                }
            name
        } catch (e: ExposedSQLException) {
            throw ConflictError(e.cause?.message)
        }
    }

    override suspend fun deleteUser(user: User): Boolean = dbQuery {
        Users.deleteWhere { Users.email eq user.email } > 0
    }

    override suspend fun userAliases(user: User): List<Alias> = dbQuery {
        Aliases
            .select { Aliases.destination eq user.email }
            .map(::resultRowToAlias)
    }

    @Throws(NotFoundError::class)
    override suspend fun alias(user: User, name: String): Alias = dbQuery {
        val email = getAlias(user, name)
        Aliases
            .select { Aliases.email eq email }
            .map(::resultRowToAlias)
            .singleOrNull() ?: throw NotFoundError("Alias", "email=$email")
    }

    @Throws(ConflictError::class)
    override suspend fun addAlias(user: User, name: String, wildcard: Boolean): Alias = dbQuery {
        try {
            val insertStatement = Aliases.insert {
                it[email] = getAlias(user, name)
                it[localPart] = name
                it[domainName] = user.domain
                it[createdAt] = LocalDate.now()
                it[this.wildcard] = wildcard
                it[destination] = user.email
            }

            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToAlias) ?: throw ConflictError()
        } catch (e: ExposedSQLException) {
            throw ConflictError(e.cause?.message)
        }
    }

    override suspend fun renameAlias(alias: Alias, name: String): String = dbQuery {
        try {
            Aliases
                .update({ Aliases.email eq alias.email }) {
                    it[this.localPart] = name
                    it[email] = "$name@${alias.domainName}"
                }
            name
        } catch (e: ExposedSQLException) {
            throw ConflictError(e.cause?.message)
        }
    }

    override suspend fun deleteAlias(alias: Alias): Boolean = dbQuery {
        Aliases.deleteWhere { Aliases.email eq alias.email } > 0
    }
}

val dao: Facade = FacadeImpl()