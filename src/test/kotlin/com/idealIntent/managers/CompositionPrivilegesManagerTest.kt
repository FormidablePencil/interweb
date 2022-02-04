package com.idealIntent.managers

import com.idealIntent.repositories.collectionsGeneric.CompositionPrivilegesRepository
import com.idealIntent.repositories.collectionsGeneric.CompositionsGenericPrivileges
import com.idealIntent.repositories.profile.AuthorRepository
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
    val authorId = 432
    val author = IAuthorEntity {
        val id = authorId
        val username = privilegedAuthor.username
    }

    val compositionPrivilegesManager = spyk(
        CompositionPrivilegesManager(
            authorRepository = authorRepository,
            compositionPrivilegesRepository = compositionPrivilegesRepository
        )
    )

    given("giveMultipleAuthorsPrivilegesByUsername") {
//            val data = createAuthorsAndGivePrivileges()
        And("provided a username that does not exist in db") {
            then("revert transaction") {
                // region setup
                every {
                    compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthors[0], privilegeId)
                } returns false
                // endregion

                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(privilegedAuthors, privilegeId)
            }
        }
        And("provided a username that does exist") {
            then("username exists in db revert transaction") {
                // region setup
                every {
                    compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthors[0], privilegeId)
                } returns true
                // endregion

                compositionPrivilegesManager.giveMultipleAuthorsPrivilegesByUsername(privilegedAuthors, privilegeId)
            }
        }
    }

    given("giveAnAuthorPrivilegesByUsername") {
        then("provided username that does not exist in db") {
            // region setup
            every { authorRepository.getByUsername(privilegedAuthor.username) } returns null
            // endregion

            val res = compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthor, privilegeId)

            res shouldBe false
        }
        then("provided username that does exist") {
            // region setup
            every { authorRepository.getByUsername(privilegedAuthor.username) } returns author
//            justRun {
//                compositionPrivilegesRepository.giveAnAuthorPrivilege(
//                    CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
//                    authorId = authorId,
//                    privilegeId = privilegeId
//                )
//            }
            // endregion

            compositionPrivilegesManager.giveAnAuthorPrivilegesByUsername(privilegedAuthor, privilegeId)

            verify(exactly = 1) {
                compositionPrivilegesRepository.giveAnAuthorPrivilege(
                    CompositionsGenericPrivileges(modify = privilegedAuthor.modify, view = privilegedAuthor.view),
                    privilegeId = privilegeId,
                    authorId = authorId
                )
            }
        }
    }
})