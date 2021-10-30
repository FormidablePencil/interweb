package routes

import exceptions.GenericError
import exceptions.ServerErrorException
import exceptions.httpRespond
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

// TODO test the success response, ServerErrorException response and Exception response
suspend fun routeRespond(call: ApplicationCall, code: () -> Any) {
    try {
        call.respond(code()) // TODO this might return too much information or non at all
    } catch (ex: ServerErrorException) {
        ex.httpRespond(call)
    } catch (ex: Exception) {
        call.respond(HttpStatusCode.InternalServerError, GenericError.ServerError)
    }
}