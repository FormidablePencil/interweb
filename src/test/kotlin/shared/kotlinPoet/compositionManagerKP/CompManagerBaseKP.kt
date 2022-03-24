package shared.kotlinPoet.compositionManagerKP

import com.squareup.kotlinpoet.*
import shared.testUtils.BehaviorSpecUtRepo
import shared.testUtils.BehaviorSpecUtRepo2

data class ManagerTestBuilderDTO(
    val compositionFlow: Class<*>,
    val compositionRepo: Class<*>,
    val responseDataType: Class<*>,
    val responseData: MemberName,
    val createRequest: Class<*>,
    val composePrepared: Class<*>,
)

fun managerTestBuilder(
    dependencies: List<Class<*>>,
    dto: ManagerTestBuilderDTO,
    packageName: String,
    isCompComplex: Boolean
): FileSpec {
    val testClass = TypeSpec
        .classBuilder(dto.compositionRepo.simpleName + "TestGen")
        .superclass(
            ClassName(
                BehaviorSpecUtRepo::class.java.packageName, BehaviorSpecUtRepo2::class.java.simpleName
            )
        )
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addComment("region Setup")
                .setupDependencies(dependencies)
                .signupThenCreateComposition(compositionFlow = dto.compositionFlow) // swap out
                .addStatement("beforeEach { %M() }", MemberName("io.mockk", "clearAllMocks"))
                .addComment("endregion Setup\n")

                .compRepoTestBuilder(dto, isCompComplex)
                .build()
        )
        .build()

    return FileSpec.builder(packageName, dto.compositionRepo.simpleName + "TestGen")
        .addComment(
            """
            =========
            Please do not make changes to this file directly. This is a generated file.
            
            This is a test of composition repository - ${dto.compositionRepo.simpleName}.
            =========
        """.trimIndent()
        )
        .addType(testClass)
        .build()
}

fun FunSpec.Builder.setupDependencies(dependencies: List<Class<*>>): FunSpec.Builder {
    val koinInject = MemberName("org.koin.core.component", "inject")

    dependencies.forEach { dependency ->
        this.addStatement(
            "val %N: %T by %M()",
            dependency.simpleName.replaceFirstChar { it.lowercase() },
            dependency,
            koinInject
        )
    }
    return this.addCode("\n")
}

// region todo - moved to BehaviorSpecUtRepo. Delete
//fun TypeSpec.Builder.setupTest(): TypeSpec.Builder {
//    val contentToString = MemberName(
//        "org.koin.core.component",
//        "inject"
//    )
//    val list = ClassName("kotlin.collections", "List")
//    val koinListener = ClassName("io.kotest.koin", "KoinListener")
//    val flowModule = ClassName("shared.DITestHelper", "FlowModule")
//    val coreModule = ClassName("shared.DIHelper", "CoreModule")
//    val instancePerTest = ClassName("instanceio.kotest.core.spec", "IsolationMode.InstancePerTest")
//
//    return this.addFunction(
//        FunSpec.builder("listeners")
//            .addModifiers(KModifier.PRIVATE)
//            .addModifiers(KModifier.OVERRIDE)
//            .addCode("return listOf(%T(listOf(%T, %T)))", koinListener, flowModule, coreModule)
//            .build()
////            .delegate(
////            CodeBlock.builder()
////                .addStatement("%M()", contentToString)
////                .build()
//    ).addFunction(
//        FunSpec.builder("isolationMode")
//            .addModifiers(KModifier.PRIVATE)
//            .addModifiers(KModifier.OVERRIDE)
////            .returns(IsolationMode::class)
//            .addStatement("return %T.${IsolationMode.InstancePerTest}", IsolationMode::class.java)
//            .build()
//    )
//}
// endregion