package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import org.koin.test.inject
import shared.DITestHelper
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.privilegedAuthors
import shared.testUtils.rollback
import java.sql.SQLIntegrityConstraintViolationException

class CompositionPrivilegesRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(listOf(DIHelper.CoreModule, DITestHelper.FlowModule)))
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val compositionPrivilegesRepository: CompositionPrivilegesRepository by inject()
    private val signupFlow: SignupFlow by inject()

    suspend fun createAuthorsAndGivePrivileges(): List<Triple<PrivilegedAuthor, Int, CreateAuthorRequest>> =
        privilegedAuthors.map {
            val createAuthorRequest = CreateAuthorRequest(
                email = it.username + "@gmail.com",
                firstname = it.username,
                lastname = it.username,
                password = it.username + "Q123!",
                username = it.username
            )
            return@map Triple(it, signupFlow.signupReturnId(createAuthorRequest), createAuthorRequest)
        }

    init {
        beforeEach {
            // create authors first
        }

        xgiven("getRecordToCollectionInfo") { }

        given("giveAnAuthorPrivilege") {
            then("not valid author ids should throw") {
                rollback {
                    // region setup
                    val randomIdOfUserThatDoesNotExist = 99999999
                    val compositionsGenericPrivileges = CompositionsGenericPrivileges(
                        modify = privilegedAuthors[0].modify,
                        view = privilegedAuthors[0].view,
                    )
                    val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()
                    // endregion

                    shouldThrow<SQLIntegrityConstraintViolationException> {
                        compositionPrivilegesRepository.giveAnAuthorPrivilege(
                            compositionsGenericPrivileges,
                            privilegeId = privilegeSourceId,
                            authorId = randomIdOfUserThatDoesNotExist
                        )
                    }
                }
            }

            then("provided existing author ids but an invalid privilege source should throw") {
                rollback {
                    // region setup - Create accounts
                    val (privilegedAuthor: PrivilegedAuthor, authorId, authorData: CreateAuthorRequest) = createAuthorsAndGivePrivileges()[0]
                    val compositionsGenericPrivileges = CompositionsGenericPrivileges(
                        modify = privilegedAuthor.modify,
                        view = privilegedAuthor.view,
                    )
                    val randomIdOfPrivilegeSourceThatDoesNotExist = 99999999
                    // endregion

                    shouldThrow<SQLIntegrityConstraintViolationException> {
                        compositionPrivilegesRepository.giveAnAuthorPrivilege(
                            compositionsGenericPrivileges,
                            privilegeId = randomIdOfPrivilegeSourceThatDoesNotExist,
                            authorId = authorId
                        )
                    }
                }
            }

            then("provided existing author ids and a privilege source") {
                rollback {
                    // region setup - Create accounts and create a privilege source
                    val (privilegedAuthor: PrivilegedAuthor, authorId, authorData: CreateAuthorRequest) = createAuthorsAndGivePrivileges()[0]
                    val compositionsGenericPrivileges = CompositionsGenericPrivileges(
                        modify = privilegedAuthor.modify,
                        view = privilegedAuthor.view,
                    )
                    val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()
                    // endregion

                    compositionPrivilegesRepository.giveAnAuthorPrivilege(
                        compositionsGenericPrivileges,
                        privilegeId = privilegeSourceId,
                        authorId = authorId
                    )
                    compositionPrivilegesRepository.checkIfPrivileged(privilegeSourceId, authorId)
                }
            }
        }

        xgiven("addPrivilegeSource") { }

        xgiven("updateRecord") { }

        xgiven("batchUpdateRecords") { }

        xgiven("deleteRecord") { }

        xgiven("deleteAllRecordsInCollection") { }

        xgiven("disassociateRecordFromCollection") { }

        xgiven("deleteCollectionButNotRecord") { }

        xgiven("batchCreateRecordToCollectionRelationship") { }

        xgiven("createRecordToCollectionRelationship") { }

        xgiven("getRecordOfCollection") { }

        xgiven("getRecordsQuery") { }
    }
}