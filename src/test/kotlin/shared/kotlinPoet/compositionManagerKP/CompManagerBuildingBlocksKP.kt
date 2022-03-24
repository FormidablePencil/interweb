package shared.kotlinPoet.compositionRepositoryKP

import com.idealIntent.exceptions.CompositionCode
import com.idealIntent.exceptions.CompositionException
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import shared.kotlinPoet.BehaviorSpecBuildingBlocksKP.given
import shared.kotlinPoet.BehaviorSpecBuildingBlocksKP.then
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

fun FunSpec.Builder.compComplexRepoTestBuilder(
    compositionRepoMember: String,
    map: MutableMap<String, Any?>
): FunSpec.Builder {
    return this
        .given("getOnlyTopLvlIdsOfCompositionOnlyModifiable", map) {
            then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
                """
                val (compositionSourceId, _, _) = signup_then_createComposition(true)
                
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
}

fun FunSpec.Builder.compRepoTestBuilder(
    data: RepositoryTestBuilderDTO,
    isCompComplex: Boolean
): FunSpec.Builder {
    val rollback = MemberName("shared.testUtils", "rollback")
    val failure = MemberName("io.kotest.assertions", "failure")
    val shouldBe = MemberName("io.kotest.matchers", "shouldBe")
    val shouldNotBe = MemberName("io.kotest.matchers", "shouldNotBe")
    val shouldThrowExactly = MemberName("io.kotest.assertions.throwables", "shouldThrowExactly")

    val map: MutableMap<String, Any?> = data.asMap().toMutableMap()
    map += "rollback" to rollback
    map += "failure" to failure
    map += "shouldBe" to shouldBe
    map += "shouldNotBe" to shouldNotBe
    map += "shouldThrowExactly" to shouldThrowExactly
    map += "compositionException" to CompositionException::class.java
    map += "compositionCode" to CompositionCode::class.java

    val compositionRepo = MemberName(data.compositionRepo.packageName, data.compositionRepo.simpleName)
    val compositionRepoMember = compositionRepo.simpleName.replaceFirstChar { it.lowercase() }
    val compositionFlow = MemberName(data.compositionFlow.packageName, data.compositionFlow.simpleName)
    val compositionFlowMember = compositionFlow.simpleName.replaceFirstChar { it.lowercase() }

    if (isCompComplex)
        this.compComplexRepoTestBuilder(compositionRepoMember, map)

    return this
        .given("getPublicComposition", map) {

            then("failed to get because composition is private") {
                """
                val (compositionSourceId, _, _) = signup_then_createComposition(false)

                $compositionRepoMember.getPublicComposition(
                    compositionSourceId = compositionSourceId
                ) %shouldBe:M null 
                """.trimIndent()
            } + then("successfully got public composition") {
                """
                    val (compositionSourceId, _, _) = signup_then_createComposition(true)

                    val res: %responseDataType:T = $compositionRepoMember.getPublicComposition(
                        compositionSourceId = compositionSourceId
                    ) ?: throw %failure:M("failed to get composition")

                    %compositionFlow:T.validateDataResponse(res)
                """.trimIndent()
            }
        }
        .given("getPrivateComposition", map) {

            then("successfully got private composition") {
                """
                val (compositionSourceId, _, authorId) = signup_then_createComposition(false)

                val res: %responseDataType:T =$compositionRepoMember.getPrivateComposition(
                    compositionSourceId, authorId
                ) ?: throw failure("failed to get composition")

                %compositionFlow:T.validateDataResponse(res)
            """.trimIndent()
            }
        }

        .given("compose", map) {
            then(
                "successfully composed collections and compositions as one composition"
            ) {
                """
                val (preparedComposition, sourceId) = $compositionFlowMember.prepareComposition(%responseData:M)

                $compositionRepoMember.compose(preparedComposition, sourceId)
            """.trimIndent()

            }
        }
        .given("deleteComposition", map) {
            then("failed to delete because author id provided is not privileged to delete.") {
                """
                val (compositionSourceId, _, _) = signup_then_createComposition(true)

                val ex = %shouldThrowExactly:M<%compositionException:T> {
                    $compositionRepoMember.deleteComposition(compositionSourceId, 999999999)
                }.code shouldBe %compositionCode:T.CompositionNotFound
                """.trimIndent()
            } + then("failed to delete on an id of a composition that doesn't exist") {
                """
                val (_, _, authorId) = signup_then_createComposition(true)

                val ex = shouldThrowExactly<CompositionException> {
                    $compositionRepoMember.deleteComposition(999999999, authorId)
                }.code shouldBe CompositionCode.CompositionNotFound
                """.trimIndent()
            } + then("success") {
                """
                val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                // region before deletion assertions
                val resBeforeDeletion: %responseDataType:T = $compositionRepoMember.getPublicComposition(
                    compositionSourceId = compositionSourceId
                ) ?: throw failure("failed to get composition")

                %compositionFlow:T.validateDataResponse(resBeforeDeletion)
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