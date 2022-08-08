package cz.majksa.mailu.dao

import cz.majksa.mailu.dao.DatabaseFactory.dbQuery
import cz.majksa.mailu.models.*
import cz.majksa.mailu.models.Alias
import org.jetbrains.exposed.sql.*
import java.time.LocalDate

class FacadeImpl : Facade {

    override suspend fun domains(): List<Domain> = dbQuery {
        Domains.selectAll().map(::resultRowToDomain)
    }

    override suspend fun domain(name: String): Domain? = dbQuery {
        Domains
            .select { Domains.name eq name }
            .map(::resultRowToDomain)
            .singleOrNull()
    }

    override suspend fun addDomain(name: String): Domain? = dbQuery {
        val insertStatement = Domains.insert {
            it[this.name] = name
            it[createdAt] = LocalDate.now()
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToDomain)
    }

    override suspend fun renameDomain(name: String, newName: String): Boolean = dbQuery {
        Domains
            .update({ Domains.name eq name }) {
                it[this.name] = newName
            } > 0
    }

    override suspend fun deleteDomain(name: String): Boolean = dbQuery {
        Domains.deleteWhere { Domains.name eq name } > 0
    }

    override suspend fun domainUsers(name: String): List<User> = dbQuery {
        Users.select { Users.domainName eq name } .map(::resultRowToUser)
    }

    override suspend fun user(domain: Domain, name: String): User? = dbQuery {
        Users
            .select { Users.email eq getEmail(domain, name) }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun addUser(domain: Domain, name: String, displayedName: String, password: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[email] = getEmail(domain, name)
            it[this.displayedName] = displayedName
            it[localPart] = name
            it[domainName] = domain.name
            it[createdAt] = LocalDate.now()
            it[this.password] = password
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun changeUserPassword(user: User, password: String): Boolean = dbQuery {
        Users
            .update({ Users.email eq user.email }) {
                it[this.password] = password
            } > 0
    }

    override suspend fun deleteUser(domain: Domain, name: String): Boolean = dbQuery {
        Users.deleteWhere { Users.email eq getEmail(domain, name) } > 0
    }

    override suspend fun userAliases(user: User): List<Alias> = dbQuery {
        Aliases
            .select { Aliases.destination eq user.email }
            .map(::resultRowToAlias)
    }

    override suspend fun alias(user: User, name: String): Alias? = dbQuery {
        Aliases
            .select { Aliases.email eq getAlias(user, name) }
            .map(::resultRowToAlias)
            .singleOrNull()
    }

    override suspend fun addAlias(user: User, name: String, wildcard: Boolean): Alias? = dbQuery {
        val insertStatement = Aliases.insert {
            it[email] = getAlias(user, name)
            it[localPart] = name
            it[domainName] = user.domain
            it[createdAt] = LocalDate.now()
            it[this.wildcard] = wildcard
            it[destination] = user.email
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToAlias)
    }

    override suspend fun deleteAlias(user: User, name: String): Boolean = dbQuery {
        Aliases.deleteWhere { Aliases.email eq getAlias(user, name) } > 0
    }
}

val dao: Facade = FacadeImpl()