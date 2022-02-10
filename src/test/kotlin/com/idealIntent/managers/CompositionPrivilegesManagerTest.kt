package com.idealIntent.managers

import com.idealIntent.configurations.AppEnv
import com.idealIntent.exceptions.CompositionCode.FailedToFindAuthorByUsername
import com.idealIntent.exceptions.CompositionCode.UserNotPrivileged
import com.idealIntent.exceptions.CompositionException
import com.idealIntent.repositories.collectionsGeneric.CompositionSourceRepository
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
    val compositionSourceRepository: CompositionSourceRepository = mockk()
    val appEnv: AppEnv = mockk()

    val sourceId = 5432
    val privilegedAuthor = privilegedAuthors[0]
    val userId = 432
    val author = IAuthorEntity {}

    val compositionPrivilegesManager = spyk(
        CompositionPrivilegesManager(
            appEnv = appEnv,
            authorRepository = authorRepository,
            compositionPrivilegesRepository = compositionSourceRepository,
        ), recordPrivateCalls = true
    )

    beforeEach { clearAllMocks() }

    given("createPrivileges") {
        then("success. User gets absolute privileges.") {
            // region setup
            every { compositionSourceRepository.addCompositionSource(name = "my composition", compositionType = 0) } returns sourceId
            justRun {
                compositionSourceRepository.giveAnAuthorPrivilegeToComposition(
                    CompositionsGenericPrivileges(modify = 1, view = 1), sourceId, userId
                )
            }
            // endregion

            compositionPrivilegesManager.createCompositionSource(compositionType = 0, userId)

            verify { compositionSourceRepository.addCompositionSource(name = "my composition", compositionType = 0) }
            verify {
                compositionSourceRepository.giveAnAuthorPrivilegeToComposition(
                    CompositionsGenericPrivileges(modify = 1, view = 1), sourceId, userId
                )
            }
        }
    }

    given("giveMultipleAuthorsPrivilegesByUsername") {
        beforeEach {
            // region setup
            appEnvMockHelper(appEnv)
            every { compositionSourceRepository.isUserPrivilegedToModifyComposition(sourceId, userId) } returns true
            every { authorRepository.getByUsername(any()) } returns author
            justRun {
                compositionSourceRepository.giveAnAuthorPrivilegeToComposition(
                    privileges = any(),
                    compositionSourceId = sourceId,
                    authorId = any()
                )
            }
            // endregion
        }

        then("user not privileged") {
            // region setup
            every { compositionSourceRepository.isUserPrivilegedToModifyComposition(sourceId, userId) } returns false
            // endregion

            val ex = shouldThrow<CompositionException> {
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    privilegedAuthors, sourceId, userId
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
                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                    privilegedAuthors, sourceId, userId
                )
            }

            verify(exactly = 1) { appEnv.database.useTransaction {} } // verify it is wrapped in a useTransaction
            ex.code shouldBe FailedToFindAuthorByUsername
            ex.moreDetails shouldBe privilegedAuthor.username
        }

        then("success") {
            compositionPrivilegesManager.giveMultipleAuthorsPrivilegesToCompositionByUsername(
                privilegedAuthors, sourceId, userId
            )

            verify(exactly = 1) { appEnv.database.useTransaction {} } // verify it is wrapped in a useTransaction
            verify { compositionSourceRepository.isUserPrivilegedToModifyComposition(sourceId, userId) }
            verify { authorRepository.getByUsername(any()) }
            verify {
                compositionSourceRepository.giveAnAuthorPrivilegeToComposition(
                    privileges = any(), compositionSourceId = sourceId, authorId = any()
                )
            }
        }
    }


    given("giveAnAuthorPrivilegesByUsername") {
        beforeEach {
            // region setup
            every { compositionSourceRepository.isUserPrivilegedToModifyComposition(sourceId, userId) } returns true
            every { authorRepository.getByUsername(privilegedAuthor.username) } returns author
            justRun {
                compositionSourceRepository.giveAnAuthorPrivilegeToComposition(
                    CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
                    compositionSourceId = sourceId,
                    authorId = author.id
                )
            }
            // endregion
        }

        then("user not privileged") {
            // region setup
            every { compositionSourceRepository.isUserPrivilegedToModifyComposition(sourceId, userId) } returns false
            // endregion

            val ex = shouldThrow<CompositionException> {
                compositionPrivilegesManager.giveAnAuthorPrivilegesToCompositionSourceByUsername(
                    privilegedAuthor,
                    sourceId,
                    userId
                )
            }

            ex.code shouldBe UserNotPrivileged
        }

        then("failed to find author by username to give privileges to") {
            // region setup
            every { authorRepository.getByUsername(privilegedAuthor.username) } returns null
            // endregion

            val ex = shouldThrow<CompositionException> {
                compositionPrivilegesManager.giveAnAuthorPrivilegesToCompositionSourceByUsername(
                    privilegedAuthor,
                    sourceId,
                    userId
                )
            }

            ex.code shouldBe FailedToFindAuthorByUsername
        }

        then("success") {
            compositionPrivilegesManager.giveAnAuthorPrivilegesToCompositionSourceByUsername(privilegedAuthor, sourceId, userId)

            verify { compositionSourceRepository.isUserPrivilegedToModifyComposition(sourceId, userId) }
            verify { authorRepository.getByUsername(privilegedAuthor.username) }
            verify {
                compositionSourceRepository.giveAnAuthorPrivilegeToComposition(
                    CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
                    compositionSourceId = sourceId,
                    authorId = author.id
                )
            }
        }
    }
})