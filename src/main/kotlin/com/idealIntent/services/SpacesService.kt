package com.idealIntent.services

import com.idealIntent.repositories.compositions.SpaceRepository

class SpacesService(
    val spaceRepository: SpaceRepository
) {
    fun getSpace(spaceAddress: String) {
//        spaceRepository.giveAuthorPrivilegesToPrivateSpace()
    }
}