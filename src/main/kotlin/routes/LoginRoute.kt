package routes

import domainServices.TokenDomainService
import dto.Token.AuthenticateRequest
import dto.login.Login
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import org.valiktor.ConstraintViolationException
import repositories.IAuthorRepository

fun Application.registerLoginRoutes() {
    routing {
        loginRoute()
    }
}

fun Route.loginRoute() {
    val tokenDomainService: TokenDomainService by inject()
    val authorRepository: IAuthorRepository by inject()

    post("/login") {
        try {
        val user = call.receive<Login>()

            println("printed yes success ok okay")

            var token = tokenDomainService.Authenticate(AuthenticateRequest(user.username, user.password))

//        call.respond(hashMapOf("token" to token))
        }
        catch (ex: ConstraintViolationException) {
            call.respond(HttpStatusCode.BadRequest, FailedRequestValidationResponse(ex))
        }
    }
}

// jwt token is just a scrambled secret and hashedPassword (I believe hashedPassword... could be not, safe, idk)
// and only the actual secret and hashedPassword can unscramble it
