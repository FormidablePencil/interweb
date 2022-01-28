package com.idealIntent.services

import dtos.portfolio.GetLayoutResult
import models.portfolio.LayoutComponent
import com.idealIntent.managers.AuthorsPortfolioManager
import models.profile.Author
import com.idealIntent.repositories.profile.AuthorRepository

class AuthorsPortfolioService(
    private val authorRepository: AuthorRepository,
    private val authorsPortfolioManager: AuthorsPortfolioManager,
) {
    fun GetLayout(authorId: Int, layoutId: Int): GetLayoutResult {
        authorsPortfolioManager.GetLayout(layoutId)
        val listOfComponents = listOf(LayoutComponent(1))
        val componentArrangement = listOf(1, 2)
        return GetLayoutResult(listOfComponents, componentArrangement)
    }

    fun GetAuthorsLayouts(authorId: Int): List<Int> {
        authorsPortfolioManager.GetAuthorsLayouts(authorId)
        return listOf<Int>(32, 43)
    }

    fun GetAuthorById(userId: Int): Author? {
        return authorRepository.getById(userId)
    };

//    fun GetAuthorByEmail(email: String): Author? {
////        return authorRepository.getByEmail(email)
//    };
}