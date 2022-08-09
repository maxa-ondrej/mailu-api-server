package cz.majksa.mailu.dao

import cz.majksa.mailu.models.Alias
import cz.majksa.mailu.models.Domain
import cz.majksa.mailu.models.User

interface Facade {
    suspend fun domains(): List<Domain>

    @Throws(NotFoundError::class)
    suspend fun domain(name: String): Domain
    @Throws(ConflictError::class)
    suspend fun addDomain(name: String): Domain
    @Throws(ConflictError::class)
    suspend fun renameDomain(domain: Domain, name: String): String
    suspend fun deleteDomain(domain: Domain): Boolean
    suspend fun domainUsers(domain: Domain): List<User>


    @Throws(NotFoundError::class)
    suspend fun user(domain: Domain, name: String): User
    @Throws(ConflictError::class)
    suspend fun addUser(domain: Domain, name: String, displayedName: String, password: String): User
    suspend fun changeUserPassword(user: User, password: String): Boolean
    @Throws(ConflictError::class)
    suspend fun renameUser(user: User, name: String): String
    suspend fun deleteUser(user: User): Boolean
    suspend fun userAliases(user: User): List<Alias>


    @Throws(NotFoundError::class)
    suspend fun alias(user: User, name: String): Alias
    @Throws(ConflictError::class)
    suspend fun addAlias(user: User, name: String, wildcard: Boolean): Alias
    @Throws(ConflictError::class)
    suspend fun renameAlias(alias: Alias, name: String): String
    suspend fun deleteAlias(alias: Alias): Boolean
}