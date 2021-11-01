package routes

import dtos.ApiDataResponse
import exceptions.GenericError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

// TODO test the success response, ServerErrorException response and Exception response
suspend fun <Data, Enum, ClassExtendedFrom> routeRespond(
    call: ApplicationCall,
    code: () -> ApiDataResponse<Data, Enum, ClassExtendedFrom>
) {
    try {
        val result = code()
        call.respond(result.statusCode()!!, result.message()!!)
    } catch (ex: Exception) {
        // todo log the exception if it isn't already being logged
        call.respond(HttpStatusCode.InternalServerError, GenericError.ServerError)
    }
}