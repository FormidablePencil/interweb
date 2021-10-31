package routes

import org.valiktor.ConstraintViolationException

fun FailedRequestValidationResponse(ex: ConstraintViolationException): String {
    var concatenatedMessage: String? = null

    ex.constraintViolations
    .map { "${it.property}: ${it.constraint.name}" }
    .forEach{f ->
        if (concatenatedMessage.isNullOrEmpty()) concatenatedMessage = f
        else "$f, $concatenatedMessage".also { concatenatedMessage = it }
    }

    if (concatenatedMessage.isNullOrEmpty()) throw Exception("Server code")
   else return concatenatedMessage as String
}