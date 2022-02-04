package com.idealIntent.managers

import com.idealIntent.exceptions.CompositionCode.FailedToFindAuthorByUsername
import com.idealIntent.exceptions.CompositionCode.UserNotPrivileged
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.CompositionsGenericPrivileges
import com.idealIntent.repositories.profile.AuthorRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import models.profile.IAuthorEntity
import shared.testUtils.privilegedAuthors

class CompositionPrivilegesManagerTest : BehaviorSpec({
    val authorRepository: AuthorRepository = mockk()
    val compositionPrivilegesRepository: CompositionPrivilegesRepository = mockk()
    val privilegeId = 5432
    val privilegedAuthor = privilegedAuthors[0]
    val userId = 432
    val author = IAuthorEntity {
        val id = 1232
        val username = privilegedAuthor.username
    }

    val compositionPrivilegesManager = spyk(
        CompositionPrivilegesManager(
            authorRepository = authorRepository,
            compositionPrivilegesRepository = compositionPrivilegesRepository
        )
    )

    // TODO("Exceptions implemented at low level, now need to be handled")
    given("giveMultipleAuthorsPrivilegesByUsername") {
//            val data = createAuthorsAndGivePrivileges()
        And("user not permitted") {
            then("revert transaction") {
                // region setup
                every {
                    compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(
                        privilegedAuthors[0], privilegeId, userId
                    )
                } throws CompositionException(UserNotPrivileged)
                // endregion

                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeId, userId
                )
            }
        }

        And("provided a username that does not exist in db") {
            then("revert transaction") {
                // region setup
                every {
                    compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(
                        privilegedAuthors[0], privilegeId, userId
                    )
                } throws CompositionException(FailedToFindAuthorByUsername)
                // endregion

                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeId, userId
                )
            }
        }

        And("provided a username that does exist") {
            then("username exists in db revert transaction") {
                // region setup
//                justRun {
//                    compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(
//                        privilegedAuthors[0], privilegeId, userId
//                    )
//                }
                // endregion

                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeId, userId
                )
            }
        }
    }

    given("giveAnAuthorPrivilegesByUsername") {
        beforeEach {
            // region setup
            every { compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId) } returns true
            every { authorRepository.getByUsername(privilegedAuthor.username) } returns author
            // endregion setup
        }
        then("user not privileged") {
            // region setup
            every { compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId) } returns false
            // endregion

            val ex = shouldThrow<CompositionException> {
                compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthor, privilegeId, userId)
            }

            ex.code shouldBe UserNotPrivileged
        }
        then("failed to find author by username to give privileges to") {
            // region setup
            every { authorRepository.getByUsername(privilegedAuthor.username) } returns null
            // endregion

            val ex = shouldThrow<CompositionException> {
                compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthor, privilegeId, userId)
            }

            ex.code shouldBe FailedToFindAuthorByUsername
        }
        then("successfully gave author privileges") {
            compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthor, privilegeId, userId)

            verify(exactly = 1) {
                compositionPrivilegesRepository.giveAnAuthorPrivilege(
                    CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
                    privilegeId = privilegeId,
                    authorId = author.id
                )
            }
        }
    }
})