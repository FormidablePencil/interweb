package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.repositories.profile.AuthorRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import shared.testUtils.privilegedAuthors

class CompositionPrivilegesRepositoryTest : BehaviorSpec() {
    private val authorRepository: AuthorRepository = mockk()
    private val compositionPrivilegesRepository: CompositionPrivilegesRepository = mockk()

    init {
        val privilegedAuthors = privilegedAuthors

        given("getPrivilegesByAuthorId") { }

        given("getRecordToCollectionInfo") { }

        given("giveAnAuthorPrivilege") {
        }

        given("giveMultipleAuthorsPrivilegesByUsername") { }

        given("addPrivilegeSource") { }

        given("updateRecord") { }

        given("batchUpdateRecords") { }

        given("deleteRecord") { }

        given("deleteAllRecordsInCollection") { }

        given("disassociateRecordFromCollection") { }

        given("deleteCollectionButNotRecord") { }

        given("batchCreateRecordToCollectionRelationship") { }

        given("createRecordToCollectionRelationship") { }

        given("getRecordOfCollection") { }

        given("getRecordsQuery") { }
    }
}
