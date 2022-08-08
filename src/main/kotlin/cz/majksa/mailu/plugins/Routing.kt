package cz.majksa.mailu.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cz.majksa.mailu.api.error
import cz.majksa.mailu.api.success
import cz.majksa.mailu.dao.dao
import cz.majksa.mailu.models.CreateUser
import cz.majksa.mailu.models.Security
import cz.majksa.mailu.models.getEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(security: Security) {
    routing {
        authenticate("auth-jwt") {
            route("/domain") {
                get {
                    success(call, dao.domains())
                }
                post {
                    val input = call.receive<Map<String, String>>()
                    val name = input["name"] ?: return@post error(
                        call,
                        HttpStatusCode.BadRequest,
                        "Name not specified"
                    )
                    val domain = dao.addDomain(name) ?: return@post error(
                        call,
                        HttpStatusCode.Conflict,
                        "Something went wrong"
                    )
                    success(call, domain)
                }
                route("{domain}") {
                    get {
                        val name = call.parameters["domain"] ?: ""
                        val domain = dao.domain(name) ?: return@get error(
                            call,
                            HttpStatusCode.NotFound,
                            "No domain with name $name"
                        )

                        success(call, domain)
                    }

                    route("/user") {
                        get {
                            val domain = call.parameters["domain"] ?: ""
                            call.respond(dao.domainUsers(domain))
                        }
                        post {
                            val createUser = call.receive<CreateUser>()
                            val domain = dao.domain(call.parameters["domain"] ?: "") ?: return@post error(
                                call,
                                HttpStatusCode.NotFound,
                                "No domain with name $createUser.domain"
                            )
                            val password = security.generatePassword(createUser.password)
                            val user = dao.addUser(domain, createUser.name, createUser.displayName, password) ?: return@post error(
                                call,
                                HttpStatusCode.Conflict,
                                "Something went wrong"
                            )
                            success(call, user)
                        }
                        route("{user}") {
                            get {
                                val domainName = call.parameters["domain"] ?: ""
                                val domain = dao.domain(domainName) ?: return@get error(
                                    call,
                                    HttpStatusCode.NotFound,
                                    "No domain with name $domainName"
                                )
                                val username = call.parameters["user"] ?: ""

                                val user =
                                    dao.user(domain, username) ?: return@get error(
                                        call,
                                        HttpStatusCode.NotFound,
                                        "No user with email ${getEmail(domain, username)}"
                                    )

                                success(call, user)
                            }
                            delete {
                                val domainName = call.parameters["domain"] ?: ""
                                val domain = dao.domain(domainName) ?: return@delete error(
                                    call,
                                    HttpStatusCode.NotFound,
                                    "No domain with name $domainName"
                                )
                                val username = call.parameters["user"] ?: ""
                                if (dao.deleteUser(domain, username)) {
                                    success(call, "deleted")
                                } else {
                                    error(
                                        call,
                                        HttpStatusCode.NotFound,
                                        "No user with email ${getEmail(domain, username)}"
                                    )
                                }
                            }

                            post("password") {
                                val input = call.receive<Map<String, String>>()
                                val password = input["password"] ?: return@post error(
                                    call,
                                    HttpStatusCode.BadRequest,
                                    "Password not specified"
                                )
                                val domainName = call.parameters["domain"] ?: ""
                                val domain = dao.domain(domainName) ?: return@post error(
                                    call,
                                    HttpStatusCode.NotFound,
                                    "No domain with name $domainName"
                                )
                                val username = call.parameters["user"] ?: ""
                                val passwordHash = security.generatePassword(password)

                                val user =
                                    dao.user(domain, username) ?: return@post error(
                                        call,
                                        HttpStatusCode.NotFound,
                                        "No user with email ${getEmail(domain, username)}"
                                    )

                                if (dao.changeUserPassword(user, passwordHash)) {
                                    success(call, buildMap {
                                        put("result", true)
                                    })
                                } else {
                                    error(
                                        call,
                                        HttpStatusCode.InternalServerError,
                                        "An error happened"
                                    )
                                }
                            }

                            route("/alias") {
                                get {
                                    val domainName = call.parameters["domain"] ?: ""
                                    val domain = dao.domain(domainName) ?: return@get error(
                                        call,
                                        HttpStatusCode.NotFound,
                                        "No domain with name $domainName"
                                    )
                                    val username = call.parameters["user"] ?: ""

                                    val user =
                                        dao.user(domain, username) ?: return@get error(
                                            call,
                                            HttpStatusCode.NotFound,
                                            "No user with email ${getEmail(domain, username)}"
                                        )
                                    call.respond(dao.userAliases(user))
                                }
                                post {
                                    val createUser = call.receive<CreateUser>()
                                    val domain = dao.domain(call.parameters["domain"] ?: "") ?: return@post error(
                                        call,
                                        HttpStatusCode.NotFound,
                                        "No domain with name $createUser.domain"
                                    )
                                    val password = security.generatePassword(createUser.password)
                                    val user = dao.addUser(domain, createUser.name, createUser.displayName, password) ?: return@post error(
                                        call,
                                        HttpStatusCode.Conflict,
                                        "Something went wrong"
                                    )
                                    success(call, user)
                                }
                                route("{user}") {
                                    get {
                                        val domainName = call.parameters["domain"] ?: ""
                                        val domain = dao.domain(domainName) ?: return@get error(
                                            call,
                                            HttpStatusCode.NotFound,
                                            "No domain with name $domainName"
                                        )
                                        val username = call.parameters["user"] ?: ""

                                        val user =
                                            dao.user(domain, username) ?: return@get error(
                                                call,
                                                HttpStatusCode.NotFound,
                                                "No user with email ${getEmail(domain, username)}"
                                            )

                                        success(call, user)
                                    }
                                    delete {
                                        val domainName = call.parameters["domain"] ?: ""
                                        val domain = dao.domain(domainName) ?: return@delete error(
                                            call,
                                            HttpStatusCode.NotFound,
                                            "No domain with name $domainName"
                                        )
                                        val username = call.parameters["user"] ?: ""
                                        if (dao.deleteUser(domain, username)) {
                                            success(call, "deleted")
                                        } else {
                                            error(
                                                call,
                                                HttpStatusCode.NotFound,
                                                "No user with email ${getEmail(domain, username)}"
                                            )
                                        }
                                    }

                                    post("rename") {
                                        val input = call.receive<Map<String, String>>()
                                        val password = input["password"] ?: return@post error(
                                            call,
                                            HttpStatusCode.BadRequest,
                                            "Password not specified"
                                        )
                                        val domainName = call.parameters["domain"] ?: ""
                                        val domain = dao.domain(domainName) ?: return@post error(
                                            call,
                                            HttpStatusCode.NotFound,
                                            "No domain with name $domainName"
                                        )
                                        val username = call.parameters["user"] ?: ""
                                        val passwordHash = security.generatePassword(password)

                                        val user =
                                            dao.user(domain, username) ?: return@post error(
                                                call,
                                                HttpStatusCode.NotFound,
                                                "No user with email ${getEmail(domain, username)}"
                                            )

                                        if (dao.changeUserPassword(user, passwordHash)) {
                                            success(call, buildMap {
                                                put("result", true)
                                            })
                                        } else {
                                            error(
                                                call,
                                                HttpStatusCode.InternalServerError,
                                                "An error happened"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
