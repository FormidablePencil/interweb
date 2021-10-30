package routes

import dtos.author.CreateAuthorRequest
import services.AuthorizationService
import dtos.login.LoginByEmailRequest
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.registerAuthorizationRoutes() {
    routing {
        authenticate("auth-jwt") {
            loginRoute()
        }
    }
}

fun Route.loginRoute() {
    val authorizationService: AuthorizationService by inject()

    post("/signup") {
        val request = call.receive<CreateAuthorRequest>()
        routeRespond(call) { authorizationService.signup(request) }
    }

    post("/login") {
        val request = call.receive<LoginByEmailRequest>()
        routeRespond(call) { authorizationService.login(request.email, request.password) }
    }

    post("/example") {
//        try {
//            val user = call.receive<LoginByEmailRequest>()
//            // return access token (expires in 15 minutes) and a refresh token (expires in 30days)
//            var token = authorizationService.login(user.email, user.password)
//            call.respond(token)
////        call.respond(hashMapOf("token" to token))
//        } catch (ex: ConstraintViolationException) {
//            call.respond(HttpStatusCode.BadRequest, FailedRequestValidationResponse(ex))
//        }
    }

    post("/refresh-token") {
//         val request =  call.receive<RefreshT>() just the refresh token
        // replace refresh token in db with a reset expirationDate token
//         respond<RefreshTokenResponse>(refreshTokenResponse) new refresh token
    }
}