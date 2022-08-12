package cz.majksa.mailu.routing

import cz.majksa.mailu.api.deleteS
import cz.majksa.mailu.api.getS
import cz.majksa.mailu.api.postS
import cz.majksa.mailu.dao.dao
import cz.majksa.mailu.models.ChangePassword
import cz.majksa.mailu.models.CreateUser
import cz.majksa.mailu.models.RenameData
import cz.majksa.mailu.models.SetAllocated
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*


fun Route.user() {
    route("/user") {
        getS {
            val domain = dao.domain(it.call.parameters["domain"] ?: "")

            dao.domainUsers(domain)
        }
        postS {
            val domain = dao.domain(it.call.parameters["domain"] ?: "")
            val input = it.call.receive<CreateUser>()
            dao.addUser(domain, input.name, input.displayName, input.password)
        }
        route("{user}") {
            getS {
                val domain = dao.domain(it.call.parameters["domain"] ?: "")
                val user = dao.user(domain, it.call.parameters["user"] ?: "")
                user
            }

            route("rename") {
                postS {
                    val input = it.call.receive<RenameData>()
                    val domain = dao.domain(it.call.parameters["domain"] ?: "")
                    val user = dao.user(domain, it.call.parameters["user"] ?: "")
                    dao.renameUser(user, input.name)
                }
            }

            route("storage") {
                postS {
                    val input = it.call.receive<SetAllocated>()
                    val domain = dao.domain(it.call.parameters["domain"] ?: "")
                    val user = dao.user(domain, it.call.parameters["user"] ?: "")
                    dao.allocateUserStorage(user, input.allocated)
                }
            }

            deleteS {
                val domain = dao.domain(it.call.parameters["domain"] ?: "")
                val user = dao.user(domain, it.call.parameters["user"] ?: "")
                dao.deleteUser(user)
            }

            route("password") {
                postS {
                    val input = it.call.receive<ChangePassword>()
                    val domain = dao.domain(it.call.parameters["domain"] ?: "")
                    val user = dao.user(domain, it.call.parameters["user"] ?: "")

                    dao.changeUserPassword(user, input.password)
                }
            }

            alias()
        }
    }
}
