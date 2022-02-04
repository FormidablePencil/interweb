package com.idealIntent.managers

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.exceptions.CompositionCode.*
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.repositories.RepositoryBase
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.CompositionsGenericPrivileges
import com.idealIntent.repositories.profile.AuthorRepository

// todo - rename RepositoryBase to database holder name
class CompositionPrivilegesManager(
    private val authorRepository: AuthorRepository,
    private val compositionPrivilegesRepository: CompositionPrivilegesRepository,
) : RepositoryBase() {

    /**
     * Give multiple authors privileges by username
     *
     * @throws CompositionException [UserNotPrivileged], [FailedToFindAuthorByUsername]
     */
    @Throws(CompositionException::class)
    fun giveMultipleAuthorsPrivilegesByUsername(
        privilegedAuthors: List<PrivilegedAuthor>, privilegeId: Int, userId: Int
    ) {
        var iteration = 0
        try {
            database.useTransaction {
                privilegedAuthors.forEach {
                    iteration++
                    giveAnAuthorPrivilegesByUsername(it, privilegeId, userId)
                }
            }
        } catch (ex: CompositionException) {
            when (ex.code) {
                UserNotPrivileged ->
                    throw CompositionException(UserNotPrivileged, ex)
                FailedToFindAuthorByUsername ->
                    throw CompositionException(FailedToFindAuthorByUsername, privilegedAuthors[iteration].username, ex)
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
     * @exception [CompositionException] [UserNotPrivileged], [FailedToFindAuthorByUsername]
     *
     * @return True for success or false for failure to get by author by username.
     */
    @Throws(CompositionException::class)
    fun giveAnAuthorPrivilegesByUsername(privilegedAuthor: PrivilegedAuthor, privilegeId: Int, userId: Int) {
        if (!compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId))
            throw CompositionException(UserNotPrivileged)

        val author = authorRepository.getByUsername(privilegedAuthor.username)
            ?: throw CompositionException(FailedToFindAuthorByUsername)

        compositionPrivilegesRepository.giveAnAuthorPrivilege(
            privileges = CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
            privilegeId = privilegeId,
            authorId = author.id
        )
    }
}