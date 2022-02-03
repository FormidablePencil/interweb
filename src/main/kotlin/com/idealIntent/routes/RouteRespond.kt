package com.idealIntent.routes

import com.idealIntent.dtos.ApiDataResponse
import com.idealIntent.exceptions.GenericError
import com.idealIntent.exceptions.ServerErrorException
import com.idealIntent.exceptions.logError
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
//            null -> throw TempException("Response was not returned with a success nor failure.", "routeRespond")
            false -> call.respond(result.statusCode()!!, result.message())
            true -> call.respond(result.data!!)
        }
    } catch (ex: ServerErrorException) {
        ex.clientMsg
    } catch (ex: Exception) {
        println(ex.message)
        logError(ex.message ?: "no message", "routeRespond")
        call.respond(HttpStatusCode.InternalServerError, GenericError.ServerError)
    }
}