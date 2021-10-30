package routes

import dtos.author.CreateAuthorRequest
import services.AuthorizationService
import dtos.login.LoginRequest
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import org.valiktor.ConstraintViolationException
import repositories.interfaces.IAuthorRepository

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
        val request = call.receive<LoginRequest>()
        routeRespond(call) { authorizationService.login(request.username, request.password) }
    }

    post("/example") {
//        try {
//            val user = call.receive<LoginRequest>()
//            // return access token (expires in 15 minutes) and a refresh token (expires in 30days)
//            var token = authorizationService.login(user.username, user.password)
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