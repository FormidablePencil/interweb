package com.idealIntent.repositories.collectionsGeneric

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.models.privileges.PrivilegeSourcesModel
import integrationTests.auth.flows.SignupFlow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import org.koin.test.inject
import org.ktorm.dsl.*
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

    private suspend fun createAuthors(): List<Triple<PrivilegedAuthor, Int, CreateAuthorRequest>> =
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
        val privileges = CompositionsGenericPrivileges(
            modify = privilegedAuthors[0].modify,
            view = privilegedAuthors[0].view,
        )

        beforeEach {
            clearAllMocks()
        }

        given("isUserPrivileged") {
            then("provided a non existing user id") {
                rollback {
                    val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()
                    val privileged = compositionPrivilegesRepository.isUserPrivileged(99999999, privilegeSourceId)
                    privileged shouldBe false
                }
            }

            then("provided a non existing privilege source id") {
                rollback {
                    val authorId = createAuthors()[0].second
                    val privileged = compositionPrivilegesRepository.isUserPrivileged(authorId, 99999999)
                    privileged shouldBe false

                }
            }

            then("success") {
                rollback {
                    // todo - privilege level
                    // region setup
                    val authorId = createAuthors()[0].second
                    val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()
                    compositionPrivilegesRepository.giveAnAuthorPrivilege(privileges, privilegeSourceId, authorId)
                    // endregion setup

                    val privileged = compositionPrivilegesRepository.isUserPrivileged(privilegeSourceId, authorId)
                    privileged shouldBe true
                }
            }
        }

        given("giveAnAuthorPrivilege") {

            then("provided a not valid author id should throw") {
                rollback {
                    // region setup
                    val randomIdOfUserThatDoesNotExist = 99999999
                    val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()
                    // endregion

                    shouldThrow<SQLIntegrityConstraintViolationException> {
                        compositionPrivilegesRepository.giveAnAuthorPrivilege(
                            privileges, privilegeSourceId, randomIdOfUserThatDoesNotExist
                        )
                    }
                }
            }

            then("provided a privilege source id non existent in db, should throw") {
                rollback {
                    // region setup - Create accounts
                    val (privilegedAuthor: PrivilegedAuthor, authorId, authorData: CreateAuthorRequest) = createAuthors()[0]
                    val randomIdOfPrivilegeSourceThatDoesNotExist = 99999999
                    // endregion

                    shouldThrow<SQLIntegrityConstraintViolationException> {
                        compositionPrivilegesRepository.giveAnAuthorPrivilege(
                            privileges, randomIdOfPrivilegeSourceThatDoesNotExist, authorId
                        )
                    }
                }
            }

            then("success") {
                rollback {
                    // region setup - Create accounts and create a privilege source
                    val (privilegedAuthor: PrivilegedAuthor, authorId, authorData: CreateAuthorRequest) = createAuthors()[0]
                    val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource()
                    // endregion

                    compositionPrivilegesRepository.giveAnAuthorPrivilege(privileges, privilegeSourceId, authorId)
                    compositionPrivilegesRepository.isUserPrivileged(privilegeSourceId, authorId)
                }
            }
        }

        given("addPrivilegeSource") {
            then("set privilege level to 3") {
                rollback {
                    // region setup
                    val privilegeLvl = 3
                    val privilegeSourceId = compositionPrivilegesRepository.addPrivilegeSource(privilegeLvl)
                    // endregion setup

                    appEnv.database.from(PrivilegeSourcesModel)
                        .select()
                        .where { (PrivilegeSourcesModel.id eq privilegeSourceId) and (PrivilegeSourcesModel.privilegeLevel eq privilegeLvl) }
                        .map { it[PrivilegeSourcesModel.id] }
                        .first() shouldNotBe null
                }
            }

            then("default privilege lvl to 0") {
                rollback {
                    // region setup
                    compositionPrivilegesRepository.addPrivilegeSource()
                    // endregion setup

                    appEnv.database.from(PrivilegeSourcesModel)
                        .select()
                        .where { PrivilegeSourcesModel.privilegeLevel eq 0 }
                        .map { it[PrivilegeSourcesModel.id] }
                        .first() shouldNotBe null
                }
            }
        }
    }
}