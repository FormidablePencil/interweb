package com.idealIntent.managers

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.exceptions.CompositionCode
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
     * @return Pair(Truthy for success or falsy for failure, username that failed to find)
     */
    fun giveMultipleAuthorsPrivilegesByUsername(
        privilegedAuthors: List<PrivilegedAuthor>, privilegeId: Int, userId: Int
    ): Pair<Boolean, String?> = database.useTransaction { transaction ->
        privilegedAuthors.forEach {
            if (giveAnAuthorPrivilegesByUsername(it, privilegeId, userId)) {
                transaction.rollback()
                return Pair(false, it.username)
            }
        }
        return Pair(true, null)
    }

    /**
     * Give an author privileges by username.
     *
     * Check if requester is privileged to privilege source. Then get the ids of usernames provided to give privileges to.
     * @throws CompositionException dfd
     * @exception CompositionException dfd
     *
     * @return True for success or false for failure to get by author by username.
     */
    fun giveAnAuthorPrivilegesByUsername(privilegedAuthor: PrivilegedAuthor, privilegeId: Int, userId: Int): Boolean {
        if (!compositionPrivilegesRepository.checkIfPrivileged(privilegeId, userId))
            throw CompositionException(CompositionCode.FailedToGivePrivilege)

        val author = authorRepository.getByUsername(privilegedAuthor.username) ?: return false

        compositionPrivilegesRepository.giveAnAuthorPrivilege(
            privileges = CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
            privilegeId = privilegeId,
            authorId = author.id
        )
        return true
    }
}