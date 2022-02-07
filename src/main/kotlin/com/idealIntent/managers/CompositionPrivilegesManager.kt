package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.CompositionsGenericPrivileges
import com.idealIntent.repositories.profile.AuthorRepository

class CompositionPrivilegesManager(
    private val appEnv: AppEnv,
    private val authorRepository: AuthorRepository,
    private val compositionPrivilegesRepository: CompositionPrivilegesRepository,
) {

    /**
     * Create privileges and assign absolute privileges to creator.
     *
     * @param authorId Id of the creator who will get absolute privileges.
     *
     * @return adds a privilege source and associates to creator's id to it.
     */
    fun createCompositionSource(compositionType: Int, authorId: Int): Int {
        val sourceId = compositionPrivilegesRepository.addCompositionSource(
            privilegeLevel = 0, name = "my composition", compositionType = compositionType
        )
        compositionPrivilegesRepository.giveAnAuthorPrivilegeToComposition(
            CompositionsGenericPrivileges(modify = 1, view = 1), sourceId, authorId
        )
        return sourceId
    }


    /**
     * Give multiple authors privileges by username
     *
     * Checks whether userId has privilege over sourceId then loops over [authors to give privileges to][privilegedAuthors]
     * and gives authors privileges.
     *
     * @throws CompositionException [UserNotPrivileged], [FailedToFindAuthorByUsername]
     */
    @Throws(CompositionException::class)
    fun giveMultipleAuthorsPrivilegesToCompositionByUsername(
        privilegedAuthors: List<PrivilegedAuthor>, compositionSourceId: Int, userId: Int
    ) {
        var iteration = 0
        try {
            appEnv.database.useTransaction {
                if (!compositionPrivilegesRepository.isUserPrivileged(compositionSourceId, userId))
                    throw CompositionException(UserNotPrivileged)

                privilegedAuthors.forEach {
                    iteration++

                    val author = authorRepository.getByUsername(it.username)
                        ?: throw CompositionException(FailedToFindAuthorByUsername)

                    compositionPrivilegesRepository.giveAnAuthorPrivilegeToComposition(
                        privileges = CompositionsGenericPrivileges(modify = it.modify, view = it.view),
                        sourceId = compositionSourceId,
                        authorId = author.id
                    )
                }
            }
        } catch (ex: CompositionException) {
            when (ex.code) {
                UserNotPrivileged ->
                    throw CompositionException(UserNotPrivileged, ex)
                FailedToFindAuthorByUsername ->
                    throw CompositionException(
                        FailedToFindAuthorByUsername, privilegedAuthors[iteration - 1].username, ex
                    )
                else ->
                    throw CompositionExceptionReport(ServerError, this::class.java, ex)
            }
        }
    }

    /**
     * Give an author privileges by username.
     *
     * Check if requester is privileged to privilege source. Then get the ids of usernames provided to give privileges to.
     *
     * @throws CompositionException [UserNotPrivileged], [FailedToFindAuthorByUsername]
     *
     * @return True for success or false for failure to get by author by username.
     */
    @Throws(CompositionException::class)
    fun giveAnAuthorPrivilegesToCompositionSourceByUsername(
        privilegedAuthor: PrivilegedAuthor,
        compositionSourceId: Int,
        userId: Int
    ) {
        if (!compositionPrivilegesRepository.isUserPrivileged(compositionSourceId, userId))
            throw CompositionException(UserNotPrivileged)

        val author = authorRepository.getByUsername(privilegedAuthor.username)
            ?: throw CompositionException(FailedToFindAuthorByUsername)

        compositionPrivilegesRepository.giveAnAuthorPrivilegeToComposition(
            privileges = CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
            sourceId = compositionSourceId,
            authorId = author.id
        )
    }
}