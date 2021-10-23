package routes

import domainServices.AuthorsPortfolioDomainService
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.registerAuthorRoutes() {
    val authorDomainService by inject<AuthorsPortfolioDomainService>()

    routing {
        signInRouting(authorDomainService)
    }
}

fun Route.signInRouting(authorDomainService: AuthorsPortfolioDomainService) {
    route("/signIn") {
        get {
            var id = 0;
            authorDomainService.GetAuthorById(id)
            // GetSettings
            // GetNewNotification
        }
    }
}
