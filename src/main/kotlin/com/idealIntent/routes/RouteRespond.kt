package com.idealIntent.routes

import dtos.ApiDataResponse
import com.idealIntent.exceptions.GenericError
import com.idealIntent.exceptions.ServerErrorException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

// TODO test the success response, ServerErrorException response and Exception response
suspend inline fun <reified Data, Enum, ClassExtendedFrom> routeRespond(
    call: ApplicationCall,
    code: () -> ApiDataResponse<Data, Enum, ClassExtendedFrom>
) {
    try {
        val result = code()
        when (result.isSuccess) {
            null -> throw ServerErrorException("Response was not returned with a success nor failure.", "routeRespond")
            false -> call.respond(result.statusCode()!!, result.message())
            true -> call.respond(result.data!!)
        }
    } catch (ex: Exception) {
        println(ex.message)
        ServerErrorException.logError(ex.message ?: "no message", "routeRespond")
        call.respond(HttpStatusCode.InternalServerError, GenericError.ServerError)
    }
}