package routes

import dtos.login.ILoginByUsernameRequest
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import serialized.CreateAuthorRequest
import serialized.LoginByUsernameRequest
import services.AuthorizationService

fun Application.registerAuthorizationRoutes() {
    routing {
//        authenticate("auth-jwt") {
        loginRoute()
//        }
    }
}



//@Serializable
//data class LoginByUsernameRequest2(val username: String, val password: String) {
//    init {
//        validate(this) {
//            validate(LoginByUsernameRequest2::username).isNotBlank()
//            validate(LoginByUsernameRequest2::password).hasSize(min = 3, max = 80)
//        }
//    }
//}

fun Route.loginRoute() {
    val authorizationService: AuthorizationService by inject()

    post("/signup") {
        val request = call.receive<CreateAuthorRequest>()
        println(request)
        routeRespond(call) { authorizationService.signup(request) }
    }

    post("/login") {
        val request = call.receive<LoginByUsernameRequest>()
        println(request)
        routeRespond(call) { authorizationService.login(request) }
    }

//    post("/login:email") {
//        val request = call.receive<LoginByEmailRequest>()
//        routeRespond(call) { authorizationService.login(request) }
//    }

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