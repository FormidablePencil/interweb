package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import com.idealIntent.exceptions.CompositionCode.FailedToFindAuthorByUsername
import com.idealIntent.exceptions.CompositionCode.UserNotPrivileged
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.CompositionsGenericPrivileges
import com.idealIntent.repositories.profile.AuthorRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import models.profile.IAuthorEntity
import shared.appEnvMockHelper
import shared.testUtils.privilegedAuthors

class CompositionPrivilegesManagerTest : BehaviorSpec({
    val authorRepository: AuthorRepository = mockk()
    val compositionPrivilegesRepository: CompositionPrivilegesRepository = mockk()
    val appEnv: AppEnv = mockk()

    val privilegeId = 5432
    val privilegedAuthor = privilegedAuthors[0]
    val userId = 432
    val author = IAuthorEntity {}

    val compositionPrivilegesManager = spyk(
        CompositionPrivilegesManager(
            appEnv = appEnv,
            authorRepository = authorRepository,
            compositionPrivilegesRepository = compositionPrivilegesRepository,
        ), recordPrivateCalls = true
    )

    beforeEach { clearAllMocks() }

    given("giveMultipleAuthorsPrivilegesByUsername") {
        beforeEach {
            // region setup
            appEnvMockHelper(appEnv)
            every { compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId) } returns true
            every { authorRepository.getByUsername(any()) } returns author
            justRun {
                compositionPrivilegesRepository.giveAnAuthorPrivilege(
                    privileges = any(),
                    privilegeId = privilegeId,
                    authorId = any()
                )
            }
            // endregion
        }

        then("user not privileged") {
            // region setup
            every { compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId) } returns false
            // endregion

            val ex = shouldThrow<CompositionException> {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeId, userId
                )
            }

            verify(exactly = 1) { appEnv.database.useTransaction {} }
            ex.code shouldBe UserNotPrivileged
        }

        then("provided a username that does not exist in db. Throws UserNotPrivileged and with it the username failed on") {
            // region setup
            every { authorRepository.getByUsername(any()) } returns null
            // endregion

            val ex = shouldThrow<CompositionException> {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                    privilegedAuthors, privilegeId, userId
                )
            }

            verify(exactly = 1) { appEnv.database.useTransaction {} } // verify it is wrapped in a useTransaction
            ex.code shouldBe FailedToFindAuthorByUsername
            ex.moreDetails shouldBe privilegedAuthor.username
        }

        then("success") {
            compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(
                privilegedAuthors, privilegeId, userId
            )

            verify(exactly = 1) { appEnv.database.useTransaction {} } // verify it is wrapped in a useTransaction
            verify { compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId) }
            verify { authorRepository.getByUsername(any()) }
            verify {
                compositionPrivilegesRepository.giveAnAuthorPrivilege(
                    privileges = any(), privilegeId = privilegeId, authorId = any()
                )
            }
        }
    }


    given("giveAnAuthorPrivilegesByUsername") {
        beforeEach {
            // region setup
            every { compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId) } returns true
            every { authorRepository.getByUsername(privilegedAuthor.username) } returns author
            justRun {
                compositionPrivilegesRepository.giveAnAuthorPrivilege(
                    CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
                    privilegeId = privilegeId,
                    authorId = author.id
                )
            }
            // endregion
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

        then("success") {
            compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthor, privilegeId, userId)

            verify { compositionPrivilegesRepository.isUserPrivileged(privilegeId, userId) }
            verify { authorRepository.getByUsername(privilegedAuthor.username) }
            verify {
                compositionPrivilegesRepository.giveAnAuthorPrivilege(
                    CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
                    privilegeId = privilegeId,
                    authorId = author.id
                )
            }
        }
    }
})