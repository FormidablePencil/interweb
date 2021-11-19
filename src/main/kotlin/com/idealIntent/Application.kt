package com.idealIntent

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import configurations.DIHelper
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.serialization.*
import org.koin.core.context.startKoin
import routes.registerAuthorRoutes
import routes.registerAuthorizationRoutes
import routes.registerCustomerRoutes
import routes.registerOrderRoutes

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    log.info("Hello from module!")


    val applicationConfig = HoconApplicationConfig(ConfigFactory.load("application.conf"))
    val port = applicationConfig.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"

//    log
    startKoin {
        // declare modules
        modules(
            DIHelper.CoreModule,
        )
    }


//    val database = Database.connect("", user = "", password = "")

//    database.insert(Employees) {
//        set(it.name, "jerry")
//        set(it.job, "trainee")
//        set(it.managerId, 1)
//        set(it.hireDate, LocalDate.now())
//        set(it.salary, 50)
//        set(it.departmentId, 1)
//    }
//
//    for (row in database.from(Employees).select()) {
//        println(row[Employees.name])
//    }

    val secret = applicationConfig.property("jwt.secret").getString()
    val issuer = applicationConfig.property("jwt.issuer").getString()
    val audience = applicationConfig.property("jwt.audience").getString()
    val myRealm = applicationConfig.property("jwt.realm").getString()
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("authorId").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
    registerOrderRoutes()
    registerAuthorRoutes()
    registerAuthorizationRoutes()
}
