package com.idealIntent.routes

import io.ktor.routing.*
import org.koin.ktor.ext.inject
import com.idealIntent.services.SpaceService

fun Route.spaceRoute() {
    val spaceService: SpaceService by inject()

get("/space:id") {//todo - create tests for spaces
        val spaceId = 1
//        routeRespond(call) { spaceService.getSpaceByAddress(spaceId) }
    }
}