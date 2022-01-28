package routes

import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import services.AuthorizationService
import services.SpaceService

fun Route.spaceRoute() {
    val spaceService: SpaceService by inject()

get("/space:id") {//todo - create tests for spaces
        val spaceId = 1
//        routeRespond(call) { spaceService.getSpaceByAddress(spaceId) }
    }
}