package routes

import domainServices.TokenDomainService
import dto.token.AuthenticateResponse
import dto.login.Login
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
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
    val tokenDomainService: TokenDomainService by inject()
    val authorRepository: IAuthorRepository by inject()

    // unit test routes/controller?
    post("/login") {

        //region authentication
        val principal = call.principal<JWTPrincipal>()
        val username = principal!!.payload.getClaim("username").asString()
        val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
        call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        //endregion

        try {
            val user = call.receive<Login>()
            // return access token (expires in 15 minutes) and a refresh token (expires in 30days)
            var token = tokenDomainService.login(AuthenticateResponse(user.username, user.password))
            call.respond(token)
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
