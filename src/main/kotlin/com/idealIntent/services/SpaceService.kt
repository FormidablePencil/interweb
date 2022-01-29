package com.idealIntent.services

import dtos.failed
import dtos.space.*
import dtos.succeeded
import com.idealIntent.helpers.RandomStringGenerator
import io.ktor.http.*
import com.idealIntent.managers.CompositionManager
import com.idealIntent.repositories.SpaceRepository
import com.idealIntent.dtos.compositions.CreateCompositionRequest
import com.idealIntent.dtos.compositions.CreateCompositionsRequest
import com.idealIntent.dtos.compositions.SingleUpdateCompositionRequest
import com.idealIntent.dtos.compositions.UpdateCompositionRequest
import com.idealIntent.dtos.space.*

// spaces (table) have components (table)

// update only one component of space
// delete only one component of space
// create only one component of space
// delete a batch of components of space
// create a batch of components of space
// create space
// update space (name of space and so forth)
// delete space and all of its components (components will still exist in library)
// don't delete space but delete all components (components will still exist in library)

// each component has special properties
// the components definitely have different data structures but most of it is for styling purposes
// the data that is rendered in can still have structure.
// data definitely needs to be stored structurally

// names to columns and tables for component data must be generic

// array of items
// multi-dimensional array of items

// may have a lot of tables just for components but a lot of it will be handled by generic named cols and tables
// how you would know what data/row is for what component would be by primary ids

// Example: OneOffGrid & FlatCarousel

// Multi-dimensional array
// outermost table - table name of which nested data is stored, id of data representing rows
// table row - primary id to foreign key off of in outermost table, table name of which nested data is stored, id to data (foreign key)
// the data - primary key for foreign key off, img, imgAlt

// === devices table ===
// primaryId: _, platform: "mac", platform_id: 321
// primaryId: _, platform: "window", platform_id: 938

// === macs table === filter by id
// id: 321, model: "macbook_pro", year: 2019

// === windows table === filter by id
// id 938, model_address: "ER334K3KJ43NLO", model_id: 653

// === model_macbook_pros table === filter by year
// year: 2019 - unique(int),
// processor: "1.4 GHz Quad-Core Intel Core i5",
// memory: "16 GB 2133 MHz LPDDR3",
// graphics: "Intel Iris Plus Graphics 645 1536 MB"


// === model_windows table === filter by id & model_address
// primaryId: _, id 653, model_address: "QQQQQQQQQQQQQQ", memory: "...", processor: "..."
// primaryId: _, id 100, model_address: "ER334K3KJ43NLO", memory: "...", processor: "..."
// primaryId: _, id 653, model_address: "ER334K3KJ43NLO", memory: "...", processor: "..." (correct)

class SpaceService(
    private val spaceRepository: SpaceRepository,
    private val componentManager: CompositionManager,
) {

    fun getSpaceByAddress(request: GetSpaceRequest): GetSpaceResponse {
        val space = spaceRepository.getSpace(request.address)
            ?: return GetSpaceResponse().failed(SpaceResponseFailed.SpaceNotFound)

        TODO()
//        return GetSpaceResponse().succeeded(HttpStatusCode.OK, space)
//        SpaceResponseData(
//
//        )
    }

    fun createSpace(createSpaceRequest: CreateSpaceRequest): CreateSpaceResponse {
        return if (spaceRepository.insertSpace(createSpaceRequest, uniqueAddress()))
            CreateSpaceResponse().succeeded(HttpStatusCode.OK)
        else
            CreateSpaceResponse().failed(CreateSpaceResponseFailed.FailedToCreateSpace)
    }

//    fun createComposition(request: CreateCompositionRequest): CreateCompositionResponse {
//        // validate that the requester has access to the space address provided
//        componentManager.createComposition(request.userComposition, request.spaceAddress)
//    }

//    fun batchCreateCompositions(request: CreateCompositionsRequest) {
//        componentManager.batchCreateCompositions(request.userCompositions, request.spaceAddress)
//    }

//    fun removeComposition(request: CreateCompositionRequest): CreateCompositionResponse {
//        // validate that the requester has access to the space address provided
//        TODO()
//        componentManager.deleteComposition(request.userComposition)
//    }

//    fun updateComposition(request: SingleUpdateCompositionRequest) {
//
//        componentManager.updateComposition(request)
//    }
//
//    fun updateCompositions(request: UpdateCompositionRequest) {
//        // todo - rollback. If one fails all revert
//        request.updateComposition.map {
//            componentManager.updateComposition(it)
//        }
//    }

    //region Create component
    //endregion

    private fun uniqueAddress(): String {
        var foundUniqueAddress = false
        var address = ""
        while (!foundUniqueAddress) {
            val potentialAddress = RandomStringGenerator.string(10)
            if (spaceRepository.getSpace(potentialAddress) == null) {
                foundUniqueAddress = true
                address = potentialAddress
            }
        }
        return address
    }

//    fun getSpacesOfAuthor(blob: String): SpaceResponse {
//        throw NotImplementedError()
//    }
//
//    fun getSpaceByAuthorsCategory(threadIds: List<Int>, category: List<String>): Thread {
//        return spaceRepository.FilterThreadByCategories(threadIds, category)
//    }
//
//    fun getSubSpace(subThreadsIds: List<Int>): List<Thread> {
//        return spaceRepository.GetThreads(subThreadsIds)
//    }
//
//    fun getRelatedSpace(relatedThreadsIds: List<Int>): List<Thread> {
//        return spaceRepository.GetThreads(relatedThreadsIds)
//    }


}