package cz.majksa.mailu.routing

import cz.majksa.mailu.api.deleteS
import cz.majksa.mailu.api.getS
import cz.majksa.mailu.api.postS
import cz.majksa.mailu.dao.dao
import cz.majksa.mailu.models.CreateAlias
import cz.majksa.mailu.models.RenameData
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*


fun Route.alias() {
    route("/alias") {
        getS {
            val domain = dao.domain(it.call.parameters["domain"] ?: "")
            val user = dao.user(domain, it.call.parameters["user"] ?: "")
            dao.userAliases(user)
        }
        postS {
            val domain = dao.domain(it.call.parameters["domain"] ?: "")
            val user = dao.user(domain, it.call.parameters["user"] ?: "")
            val input = it.call.receive<CreateAlias>()
            dao.addAlias(user, input.name, input.wildcard)
        }
        route("{alias}") {
            getS {
                val domain = dao.domain(it.call.parameters["domain"] ?: "")
                val user = dao.user(domain, it.call.parameters["user"] ?: "")
                val alias = dao.alias(user, it.call.parameters["alias"] ?: "")
                alias
            }

            route("rename") {
                postS {
                    val input = it.call.receive<RenameData>()
                    val domain = dao.domain(it.call.parameters["domain"] ?: "")
                    val user = dao.user(domain, it.call.parameters["user"] ?: "")
                    val alias = dao.alias(user, it.call.parameters["alias"] ?: "")
                    dao.renameAlias(alias, input.name)
                }
            }

            deleteS {
                val domain = dao.domain(it.call.parameters["domain"] ?: "")
                val user = dao.user(domain, it.call.parameters["user"] ?: "")
                val alias = dao.alias(user, it.call.parameters["alias"] ?: "")
                dao.deleteAlias(alias)
            }
        }
    }
}
