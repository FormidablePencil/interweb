package routes

import dtos.ApiResponse
import dtos.IApiResponseEnum
import exceptions.GenericError
import exceptions.ServerErrorException
import exceptions.httpRespond
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

// TODO test the success response, ServerErrorException response and Exception response
suspend fun <Data, FailedCode> routeRespond(call: ApplicationCall, code: () -> ApiResponse<Data, FailedCode>) {
    try {
        val result = code()
        call.respond(result.statusCode()!!, result.message()!!)
    } catch (ex: ServerErrorException) {
        ex.httpRespond(call)
    } catch (ex: Exception) {
        call.respond(HttpStatusCode.InternalServerError, GenericError.ServerError)
    }
}