package shared.kotlinPoet.compositionRepositoryKP

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import shared.kotlinPoet.TestBuilderKP.given
import shared.kotlinPoet.TestBuilderKP.then
import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> T.asMap(): Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this) }
}

fun FunSpec.Builder.signupThenCreateComposition(compositionFlow: Class<*>): FunSpec.Builder {
    return this.addCode(
        """
            suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> { 
              val authorId = signupFlow.signupReturnId()
                val layoutId = compositionService.createNewLayout(
                  name = %1N.layoutName,
                  authorId = authorId
              ).data ?: throw failure("Failed to get id of newly created layout.")
                
              val compositionSourceId = %2N.createComposition(publicView, layoutId, authorId)
              
              return Triple(compositionSourceId, layoutId, authorId)
            }
        """.trimIndent(), compositionFlow.simpleName, compositionFlow.simpleName.replaceFirstChar { it.lowercase() }
    ).addCode("\n\n")
}

fun FunSpec.Builder.compComplexRepoTestBuilder(data: RepositoryTestBuilderDTO): FunSpec.Builder {
    val rollback = MemberName("shared.testUtils", "rollback")
    val failure = MemberName("io.kotest.assertions", "failure")
    val shouldBe = MemberName("io.kotest.matchers", "shouldBe")
    val shouldNotBe = MemberName("io.kotest.matchers", "shouldNotBe")
    val shouldThrowExactly = MemberName("io.kotest.assertions.throwables", "shouldThrowExactly")
//    val createRequestData = MemberName(data.createRequestData.first, data.createRequestData.second)

    val map: MutableMap<String, Any?> = data.asMap().toMutableMap()
    map += "rollback" to rollback
    map += "failure" to failure
    map += "shouldBe" to shouldBe
    map += "shouldNotBe" to shouldNotBe
    map += "shouldThrowExactly" to shouldThrowExactly
    map += "compositionException" to CompositionException::class.java
    map += "compositionCode" to CompositionCode::class.java

    val createRequestData = MemberName(data.createRequestData.first, data.createRequestData.second)
    map += "createRequestData" to createRequestData


//    map += "createRequestData" to createRequestData

    val compositionRepo = MemberName(data.compositionRepo.packageName, data.compositionRepo.simpleName)
    val compositionRepoMember = compositionRepo.simpleName.replaceFirstChar { it.lowercase() }
//    val createRequestData =


    return this
        .given("getOnlyTopLvlIdsOfCompositionOnlyModifiable", map) {
            then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
                """
                val (compositionSourceId, _, authorId) = signup_then_createComposition(true)
                
                $compositionRepoMember.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                    compositionSourceId = compositionSourceId, authorId = 1
                ) %shouldBe:M null
                """.trimIndent()
            } + then("successfully retrieved private composition") {
                """
                val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                $compositionRepoMember.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                    compositionSourceId = compositionSourceId, authorId = authorId
                ) %shouldNotBe:M null
                """.trimIndent()
            }
        }
        .given("getPublicComposition", map) {

            then("failed to get because composition is private") {
                """
                val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(false)

                $compositionRepoMember.getPublicComposition(
                    compositionSourceId = compositionSourceId
                ) shouldBe null 
                """.trimIndent()
            } + then("successfully got public composition") {
                """
                    val (compositionSourceId, layoutId, authorId) =
                        signup_then_createComposition(true)

                    val comp: %responseData:T = $compositionRepoMember.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw %failure:M("failed to get composition")

                    // region verify
                    comp.images.size shouldBe %createRequestData:M.images.size
                    comp.imgOnclickRedirects.size shouldBe carouselBasicImagesCreateReq.imgOnclickRedirects.size
                    comp.images.forEach { resItem ->
                        carouselBasicImagesCreateReq.images.find {
                            it.orderRank == resItem.orderRank
                                    && it.description == resItem.description
                                    && it.url == resItem.url
                        } shouldNotBe null
                    }
                    comp.imgOnclickRedirects.forEach { item ->
                        carouselBasicImagesCreateReq.imgOnclickRedirects.find {
                            it.text == item.text
                                    && it.orderRank == item.orderRank
                        } shouldNotBe null
                    }
                    comp.name shouldBe carouselBasicImagesCreateReq.name
                    // endregion
                """.trimIndent()
            }
        }
        .given("getPrivateComposition", map) {

            then("successfully got private composition") {
                """
                val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(false)

                val comp: %responseData:T =
                    $compositionRepoMember.getPrivateComposition(compositionSourceId, authorId)
                        ?: throw failure("failed to get composition")

                // region verify
                comp.images.size shouldBe carouselBasicImagesCreateReq.images.size
                comp.imgOnclickRedirects.size shouldBe carouselBasicImagesCreateReq.imgOnclickRedirects.size
                comp.images.forEach { resItem ->
                    carouselBasicImagesCreateReq.images.find {
                        it.orderRank == resItem.orderRank
                                && it.description == resItem.description
                                && it.url == resItem.url
                    } shouldNotBe null
                }
                comp.imgOnclickRedirects.forEach { item ->
                    carouselBasicImagesCreateReq.imgOnclickRedirects.find {
                        it.text == item.text
                                && it.orderRank == item.orderRank
                    } shouldNotBe null
                }
                comp.name shouldBe carouselBasicImagesCreateReq.name
                // endregion 
            """.trimIndent()
            }
        }

        .given("compose", map) {
            """suspend fun prepareComposition(): %composePrepared:T {
                val authorId = signupFlow.signupReturnId()
                val createRequest = %createRequest:T(
                    name = "that was legitness",
                    images = listOf(),
                    imgOnclickRedirects = listOf(),
                    privilegedAuthors = listOf(),
                    privilegeLevel = 0,
                )
                val layoutId = spaceRepository.insertNewLayout(createRequest.name, authorId)

                val imageCollectionId = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                val redirectsCollectionId = textRepository.batchInsertRecordsToNewCollection(
                        createRequest.imgOnclickRedirects
                    )

                val compositionSourceId = compositionPrivilegesManager.createCompositionSource(
                        compositionType = 0,
                        authorId = authorId,
                        name = "legit",
                        privilegeLevel = 0,
                    )

                spaceRepository.associateCompositionToLayout(
                    orderRank = 0,
                    compositionSourceId = compositionSourceId,
                    layoutId = layoutId
                )

                return %composePrepared:T(
                    name = createRequest.name,
                    imageCollectionId = imageCollectionId,
                    redirectTextCollectionId = redirectsCollectionId,
                    sourceId = compositionSourceId,
                )
            }""" + then("successfully composed collections and compositions as one composition") {
                """
                val preparedComposition = prepareComposition()

                carouselOfImagesRepository.compose(
                    preparedComposition,
                    preparedComposition.sourceId
                )
            """.trimIndent()

            }
        }
        .given("deleteComposition", map) {
            """
            suspend fun prepareComposition(): CarouselOfImagesComposePrepared {
                val authorId = signupFlow.signupReturnId()
                val createRequest = %createRequest:T(
                    name = "that was legitness",
                    images = listOf(),
                    imgOnclickRedirects = listOf(),
                    privilegedAuthors = listOf(),
                    privilegeLevel = 0,
                )
                val layoutId = spaceRepository.insertNewLayout(createRequest.name, authorId)

                val imageCollectionId = imageRepository.batchInsertRecordsToNewCollection(createRequest.images)
                val redirectsCollectionId =
                    textRepository.batchInsertRecordsToNewCollection(createRequest.imgOnclickRedirects)

                val compositionSourceId =
                    compositionPrivilegesManager.createCompositionSource(
                        compositionType = 0,
                        authorId = authorId,
                        name = "legit",
                        privilegeLevel = 0,
                    )

                spaceRepository.associateCompositionToLayout(
                    orderRank = 0,
                    compositionSourceId = compositionSourceId,
                    layoutId = layoutId
                )

                return CarouselOfImagesComposePrepared(
                    name = createRequest.name,
                    imageCollectionId = imageCollectionId,
                    redirectTextCollectionId = redirectsCollectionId,
                    sourceId = compositionSourceId,
                )
            }
            """.trimIndent() + then("failed to delete because author id provided is not privileged to delete.") {
                """
                val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                val ex = %shouldThrowExactly:M<%compositionException:T> {
                    $compositionRepoMember.deleteComposition(compositionSourceId, 999999999)
                }.code shouldBe %compositionCode:T.CompositionNotFound
                """.trimIndent()
            } + then("failed to delete on an id of a composition that doesn't exist") {
                """
                val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                val ex = shouldThrowExactly<CompositionException> {
                    $compositionRepoMember.deleteComposition(999999999, authorId)
                }.code shouldBe CompositionCode.CompositionNotFound
                """.trimIndent()
            } + then("success") {
                """
                val (compositionSourceId, layoutId, authorId) = signup_then_createComposition(true)

                // region before deletion assertions
                val resBeforeDeletion: %responseData:T = $compositionRepoMember.getPublicComposition(
                    compositionSourceId = compositionSourceId
                ) ?: throw failure("failed to get composition")

                resBeforeDeletion.images.size shouldBe carouselBasicImagesCreateReq.images.size
                resBeforeDeletion.imgOnclickRedirects.size shouldBe carouselBasicImagesCreateReq.imgOnclickRedirects.size
                resBeforeDeletion.images.forEach { resItem ->
                    carouselBasicImagesCreateReq.images.find {
                        it.orderRank == resItem.orderRank
                                && it.url == resItem.url
                                && it.description == resItem.description
                    } shouldNotBe null
                }
                resBeforeDeletion.imgOnclickRedirects.forEach { item ->
                    carouselBasicImagesCreateReq.imgOnclickRedirects.find {
                        item.orderRank == it.orderRank
                                && item.text == it.text
                    } shouldNotBe null
                }
                resBeforeDeletion.name shouldBe carouselBasicImagesCreateReq.name
                // endregion

                $compositionRepoMember.deleteComposition(compositionSourceId, authorId)

                // region after deletion assertions
                val resAfterDeletion = $compositionRepoMember.getPublicComposition(compositionSourceId)

                resAfterDeletion shouldBe null
                // endregion
                """.trimIndent()
            }
        }
}

fun main() {

}