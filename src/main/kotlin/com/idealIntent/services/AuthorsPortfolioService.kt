package com.idealIntent.services

import com.idealIntent.dtos.space.GetLayoutResultResponse
import com.idealIntent.managers.AuthorsPortfolioManager
import com.idealIntent.repositories.profile.AuthorRepository
import models.portfolio.LayoutComponent
import models.profile.IAuthorEntity

class AuthorsPortfolioService(
    private val authorRepository: AuthorRepository,
    private val authorsPortfolioManager: AuthorsPortfolioManager,
) {
    fun GetLayout(authorId: Int, layoutId: Int): GetLayoutResultResponse {
        authorsPortfolioManager.GetLayout(layoutId)
        val listOfComponents = listOf(LayoutComponent(1))
        val componentArrangement = listOf(1, 2)
        return GetLayoutResultResponse(listOfComponents, componentArrangement)
    }

    fun GetAuthorsLayouts(authorId: Int): List<Int> {
        authorsPortfolioManager.GetAuthorsLayouts(authorId)
        return listOf<Int>(32, 43)
    }

    fun GetAuthorById(userId: Int): IAuthorEntity? {
        return authorRepository.getById(userId)
    };

//    fun GetAuthorByEmail(email: String): Author? {
////        return authorRepository.getByEmail(email)
//    };
}