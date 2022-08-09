package cz.majksa.mailu.api

import cz.majksa.mailu.dao.HTTPError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

suspend fun ApplicationCall.error(status: HttpStatusCode, message: String) {
    respond(status, Error(status.value, status.description, message))
}

suspend fun ApplicationCall.success(data: Any, status: HttpStatusCode = HttpStatusCode.OK) {
    respond(status, data)
}

typealias Context = PipelineContext<Unit, ApplicationCall>
typealias CallBody = suspend (Context) -> Any

suspend fun Context.execute(body: CallBody) =
    try {
        val response = body(this)
        call.success(
            when (response) {
                is Int -> IntResponse(response)
                is String -> StringResponse(response)
                is Boolean -> BooleanResponse(response)
                else -> response
            }
        )
    } catch (e: HTTPError) {
        call.error(e.code, e.message)
    }

fun Route.getS(body: CallBody): Route {
    return method(HttpMethod.Get) {
        handle {
            execute(body)
        }
    }
}

fun Route.postS(body: CallBody): Route {
    return method(HttpMethod.Post) {
        handle {
            execute(body)
        }
    }
}

fun Route.putS(body: CallBody): Route {
    return method(HttpMethod.Put) {
        handle {
            execute(body)
        }
    }
}

fun Route.deleteS(body: CallBody): Route {
    return method(HttpMethod.Delete) {
        handle {
            execute(body)
        }
    }
}

fun Route.headS(body: CallBody): Route {
    return method(HttpMethod.Head) {
        handle {
            execute(body)
        }
    }
}

fun Route.patchS(body: CallBody): Route {
    return method(HttpMethod.Patch) {
        handle {
            execute(body)
        }
    }
}

fun Route.optionsS(body: CallBody): Route {
    return method(HttpMethod.Options) {
        handle {
            execute(body)
        }
    }
}