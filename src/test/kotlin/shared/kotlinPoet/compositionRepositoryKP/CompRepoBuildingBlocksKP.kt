package shared.kotlinPoet.compositionRepositoryKP

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import shared.kotlinPoet.TestBuilderKP
import shared.kotlinPoet.TestBuilderKP.given
import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> T.asMap() : Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this) }
}

data class RepositoryTestBuilderDTO(
    val compositionFlow: Class<*>,
    val compositionRepo: Class<*>,
)

fun FunSpec.Builder.signupThenCreateComposition(compositionFlow: Class<*>): FunSpec.Builder {
    return this.addCode(
        """
            suspend fun signup_then_createComposition(publicView: Boolean): Triple<Int, Int, Int> { 
              val authorId = signupFlow.signupReturnId()
                val layoutId = compositionService.createNewLayout(
                  name = %1N.layoutName,
                  authorId = authorId
              ).data ?: throw failure("Failed to get id of newly created layout.")
                
              val compositionSourceId = %1N.createComposition(publicView, layoutId, authorId)
              
              return Triple(compositionSourceId, layoutId, authorId)
            }
        """.trimIndent(), compositionFlow.simpleName.replaceFirstChar { it.lowercase() }
    ).addCode("\n\n")
}

fun FunSpec.Builder.compComplexRepoTestBuilder(data: RepositoryTestBuilderDTO): FunSpec.Builder {
    val compositionRepo = MemberName(data.compositionRepo.packageName, data.compositionRepo.simpleName)
    val map: Map<String, Any?> = data.asMap()

    return this.given("getOnlyTopLvlIdsOfCompositionOnlyModifiable", map) {
        TestBuilderKP.then("Author id not privileged to view nor modify. Failed to retrieve private composition") {
            """
                val (compositionSourceId, _, authorId) = signup_then_createComposition(true)
                
                %compositionRepo:T.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                    compositionSourceId = compositionSourceId, authorId = 1
                ) shouldBe null
            """.trimIndent()
        } + TestBuilderKP.then("successfully retrieved private composition") {
            """
                val (compositionSourceId, _, authorId) = signup_then_createComposition(true)

                %compositionRepo:T.getOnlyTopLvlIdsOfCompositionByOnlyPrivilegedToModify(
                    compositionSourceId = compositionSourceId, authorId = authorId
                ) shouldNotBe null
            """.trimIndent()
        }
    }
}

fun main() {

}