package com.idealIntent.repositories.compositions

import com.idealIntent.configurations.DIHelper
import com.idealIntent.dtos.CreateAuthorRequest
import com.idealIntent.repositories.collectionsGeneric.PrivilegeRepository
import dtos.compositions.genericStructures.privileges.PrivilegedAuthor
import integrationTests.auth.flows.AuthUtilities
import integrationTests.auth.flows.SignupFlow
import io.kotest.koin.KoinListener
import io.kotest.matchers.shouldBe
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.rollback

class PrivilegeRepositoryTest : BehaviorSpecUtRepo() {
    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))

    init {
        lateinit var privilegeRepository: PrivilegeRepository
        lateinit var signupFlow: SignupFlow
        lateinit var authUtilities: AuthUtilities

        beforeEach {
            privilegeRepository = PrivilegeRepository()
            signupFlow = SignupFlow()
        }

        /**
         * Generate privileged authors to consume
         *
         * authorId must be created before it's assigned to privilege since it privilege authorId is a foreign key
         */
        suspend fun generatePrivilegedAuthorsToConsume(): List<PrivilegedAuthor> {
            return (1..3).map {
                val createAuthorRequest = CreateAuthorRequest(
                    email = "steveJohns$it@example.com",
                    firstname = "firstname",
                    lastname = "lastname",
                    password = "password",
                    username = "steveJohns$it"
                )
                signupFlow.signup(createAuthorRequest)
                val authorId = authUtilities.getAuthorIdByUsername(createAuthorRequest.username)
                PrivilegedAuthor(authorId = authorId, modLvl = 1)
            }
        }

        given("insertNewRecord") {
            then("getAssertionsById") {
                rollback {
                    val privilegesTo = "some libOfComp"
                    val privilegedAuthor = generatePrivilegedAuthorsToConsume().first()

                    val privilegeId = privilegeRepository.insertNewRecord(privilegedAuthor, privilegesTo)
                }
            }
        }

        given("batchInsertNewRecords") {
            then("getComposition") {
                rollback {
                    val privilegesTo = "some libOfComp"
                    val privilegedAuthors = generatePrivilegedAuthorsToConsume()

                    val privilegeId =
                        privilegeRepository.batchInsertNewRecords(privilegedAuthors, privilegesTo)
                            ?: throw Exception("failed to get id")
                    val res = privilegeRepository.getCollection(privilegeId)

                    res.privilegeTo shouldBe privilegesTo // todo - typo
                    res.privilegedAuthors.size shouldBe privilegedAuthors.size // todo
                    res.privilegedAuthors.map {
                        val privilegedAuthor = privilegedAuthors.find { privilegedAuthor ->
                            privilegedAuthor.authorId == it.authorId
                        } ?: throw Exception("failed to find returned image")
                        privilegedAuthor.modLvl shouldBe it.modLvl
                    }
                }
            }
            given("updateRecord") {
                then("getComposition") {}
            }
            given("batchUpdateRecords") {
                then("getComposition") {}
            }
        }
    }
}