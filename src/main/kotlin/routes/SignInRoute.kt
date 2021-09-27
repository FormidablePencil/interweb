package routes

import domainServices.AuthorDomainService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import jdk.internal.vm.compiler.word.LocationIdentity.any
import models.customerStorage
import org.koin.ktor.ext.inject
import repositories.AuthorRepository

fun Application.registerAuthorRoutes() {
    val authorDomainService by inject<AuthorDomainService>()

    routing {
        signInRouting(authorDomainService)
    }
}

fun Route.signInRouting(authorDomainService: AuthorDomainService) {
    route("/signIn") {
        get {
            var id = 0;
            authorDomainService.GetAuthor(id)
            // GetSettings
            // GetNewNotification
        }
    }
}
