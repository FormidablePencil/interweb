package com.idealIntent.services

import com.idealIntent.managers.AuthorsPortfolioManager
import dtos.portfolio.AddNewComponentRequest
import com.idealIntent.repositories.PortfolioComponentRepository

class PortfolioCreationPlaygroundService(
    private var authorsPortfolioManager: AuthorsPortfolioManager,
    private var portfolioComponentRepository : PortfolioComponentRepository,
) {
    fun GetAuthorsLayouts(authorId: Int) {
        authorsPortfolioManager.GetAuthorsLayouts(authorId)
    }

    fun GetLayout(layoutId: Int) {
        authorsPortfolioManager.GetLayout(layoutId)
    }

    fun AddNewComponent(addNewComponentRequest: AddNewComponentRequest): Int {
        portfolioComponentRepository.Post()
        return 1
    }

    fun UpdateLayoutArrangment(layoutId: Int, arrangement: List<Int>) {

    }

    fun UpdateComponent(componentId: Int) {

    }

    fun DeleteComponent(componentId: Int) {

    }

    fun GetLayoutArrangment(layoutId: Int) {

    }
    fun GetAuthorsLibraries() {
        // authorId will be given to me through token
    }

    fun GetLibraryOfComponents(libraryId: Int) {

    }

    fun GetLibraryOfAllComponents() {
        // authorId will be given to me through token
    }
}