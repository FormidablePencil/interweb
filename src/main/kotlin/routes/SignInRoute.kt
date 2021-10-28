package routes

import services.AuthorsPortfolioService
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.registerAuthorRoutes() {
    val authorDomainService by inject<AuthorsPortfolioService>()

    routing {
        signInRouting(authorDomainService)
    }
}

fun Route.signInRouting(authorDomainService: AuthorsPortfolioService) {
    route("/signIn") {
        get {
            var id = 0;
            authorDomainService.GetAuthorById(id)
            // GetSettings
            // GetNewNotification
        }
    }
}
