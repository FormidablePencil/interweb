package com.jetbrains.handson.httpapi

//import configurations.DIHelper
//import configurations.myModule
import configurations.DIHelper
import io.ktor.application.Application
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import models.Employees
import org.koin.core.context.startKoin
import org.koin.ktor.ext.modules
//import org.koin.core.context.startKoin
//import org.koin.ktor.ext.modules
import routes.registerCustomerRoutes
import routes.registerOrderRoutes
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.select
import repositories.AuthorRepository
import repositories.IAuthorRepository
import routes.registerAuthorRoutes
import java.time.LocalDate

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    // TODO move db connectionString to .env
    // TODO connection for _connectionFactory must be dependency injected
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
//        val database = Database.connect(
//            url = "",
//            user = "",
//            password = ""
//        )
//    }.start(wait = true)

//    val myModule = org.koin.dsl.module {
//        single { AuthorRepository() as IAuthorRepository }
//    }

    startKoin {
        // declare modules
        modules(DIHelper.CoreModule)
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

    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
    registerOrderRoutes()
    registerAuthorRoutes()
}
