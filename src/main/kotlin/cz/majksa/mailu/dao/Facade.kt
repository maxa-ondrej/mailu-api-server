package cz.majksa.mailu.dao

import cz.majksa.mailu.models.Alias
import cz.majksa.mailu.models.Domain
import cz.majksa.mailu.models.User

interface Facade {
    suspend fun domains(): List<Domain>
    suspend fun domain(name: String): Domain?
    suspend fun addDomain(name: String): Domain?
    suspend fun renameDomain(name: String, newName: String): Boolean
    suspend fun deleteDomain(name: String): Boolean
    suspend fun domainUsers(name: String): List<User>

    suspend fun user(domain: Domain, name: String): User?
    suspend fun addUser(domain: Domain, name: String, displayedName: String, password: String): User?
    suspend fun changeUserPassword(user: User, password: String): Boolean
    suspend fun deleteUser(domain: Domain, name: String): Boolean
    suspend fun userAliases(user: User): List<Alias>

    suspend fun alias(user: User, name: String): Alias?
    suspend fun addAlias(user: User, name: String, wildcard: Boolean): Alias?
    suspend fun deleteAlias(user: User, name: String): Boolean
}