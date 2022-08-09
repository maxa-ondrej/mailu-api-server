package cz.majksa.mailu.routing

import cz.majksa.mailu.api.deleteS
import cz.majksa.mailu.api.getS
import cz.majksa.mailu.api.postS
import cz.majksa.mailu.dao.dao
import cz.majksa.mailu.models.CreateDomain
import cz.majksa.mailu.models.RenameData
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*


fun Route.domain() {
    route("/domain") {
        getS {
            dao.domains()
        }
        postS {
            val input = it.call.receive<CreateDomain>()
            dao.addDomain(input.name)
        }
        route("{domain}") {
            getS {
                val domain = dao.domain(it.call.parameters["domain"] ?: "")
                domain
            }

            route("rename") {
                postS {
                    val input = it.call.receive<RenameData>()
                    val domain = dao.domain(it.call.parameters["domain"] ?: "")
                    dao.renameDomain(domain, input.name)
                }
            }

            deleteS {
                val domain = dao.domain(it.call.parameters["domain"] ?: "")
                dao.deleteDomain(domain)
            }

            user()
        }
    }
}
