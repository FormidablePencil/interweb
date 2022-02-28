package com.idealIntent.repositories.compositions

import shared.testUtils.BehaviorSpecUtRepo

class PrivilegeRepositoryTest : BehaviorSpecUtRepo() {
//    override fun listeners() = listOf(KoinListener(DIHelper.CoreModule))
//
//    init {
//        lateinit var privilegeRepository: PrivilegeRepository
//        lateinit var signupFlow: SignupFlow
//        lateinit var authUtilities: AuthUtilities
//
//        beforeEach {
//            privilegeRepository = PrivilegeRepository()
//            signupFlow = SignupFlow()
//        }
//
//        /**
//         * Generate privileged authors to consume
//         *
//         * authorId must be created before it's assigned to privilege since it privilege authorId is a foreign key
//         */
//        suspend fun generatePrivilegedAuthorsToConsume(): List<PrivilegedAuthor> {
//            return (1..3).map {
//                val createAuthorRequest = CreateAuthorRequest(
//                    email = "steveJohns$it@example.com",
//                    firstname = "firstname",
//                    lastname = "lastname",
//                    password = "password",
//                    username = "steveJohns$it"
//                )
//                signupFlow.signup(createAuthorRequest)
//                val authorId = authUtilities.getAuthorIdByUsername(createAuthorRequest.username)
//                PrivilegedAuthor(authorId = authorId, modLvl = 1)
//            }
//        }
//
//        given("insertNewRecord") {
//            then("getAssertionsById") {
//                rollback {
//                    val privilegesTo = "some libOfComp"
//                    val privilegedAuthor = generatePrivilegedAuthorsToConsume().first()
//
//                    val sourceId = privilegeRepository.insertNewRecord(privilegedAuthor, privilegesTo)
//                }
//            }
//        }
//
//        given("batchInsertNewRecords") {
//            then("getSingleCompositionOfPrivilegedAuthor") {
//                rollback {
//                    val privilegesTo = "some libOfComp"
//                    val privilegedAuthors = generatePrivilegedAuthorsToConsume()
//
//                    val sourceId =
//                        privilegeRepository.batchInsertNewRecords(privilegedAuthors)
//                            ?: throw Exception("failed to get id")
//                    val res = privilegeRepository.getCollectionOfRecords(sourceId)
//
//                    res.privilegeTo shouldBe privilegesTo // todo - typo
//                    res.privilegedAuthors.size shouldBe privilegedAuthors.size // todo
//                    res.privilegedAuthors.map {
//                        val privilegedAuthor = privilegedAuthors.find { privilegedAuthor ->
//                            privilegedAuthor.authorId == it.authorId
//                        } ?: throw Exception("failed to find returned imageUrl")
//                        privilegedAuthor.modLvl shouldBe it.modLvl
//                    }
//                }
//            }
//            given("updateRecord") {
//                then("getSingleCompositionOfPrivilegedAuthor") {}
//            }
//            given("batchUpdateRecords") {
//                then("getSingleCompositionOfPrivilegedAuthor") {}
//            }
//        }
//    }
}