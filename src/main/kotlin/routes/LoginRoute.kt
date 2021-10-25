package routes

import domainServices.LoginDomainService
import dto.login.Login
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import org.valiktor.ConstraintViolationException
import repositories.IAuthorRepository

fun Application.registerLoginRoutes() {
    routing {
        authenticate("auth-jwt") {
            loginRoute()
        }
    }
}

fun Route.loginRoute() {
    val loginDomainService: LoginDomainService by inject()
    val authorRepository: IAuthorRepository by inject()

    post("/login") {
        try {
            val user = call.receive<Login>()
            // return access token (expires in 15 minutes) and a refresh token (expires in 30days)
            var token = loginDomainService.login(user.username, user.password)
            call.respond(token)
//        call.respond(hashMapOf("token" to token))
        } catch (ex: ConstraintViolationException) {
            call.respond(HttpStatusCode.BadRequest, FailedRequestValidationResponse(ex))
        }
    }

    post("/refreshtoken") {
        // receive<RefreshToken>() just the refresh token
        // replace refresh token in db with a reset expirationDate token
        // respond<RefreshTokenResponse>(refreshTokenResponse) new refresh token
    }

    // test
}

// Reasoning behind refresh tokens in place of just access tokens.
// Sending request can be intercepted so. So if a request is intercepted, hopefully the key/token expires, giving them little time.
// With refresh tokens, we pass it to a minimum. Just use it for get the access tokens to reduce exposure to the long-lasting key (60 days perhaps)
