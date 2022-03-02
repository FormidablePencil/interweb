package shared.kotlinPoet

import com.squareup.kotlinpoet.*
import shared.kotlinPoet.compositionRepositoryKP.RepositoryTestBuilderDTO
import shared.kotlinPoet.compositionRepositoryKP.compComplexRepoTestBuilder
import shared.kotlinPoet.compositionRepositoryKP.signupThenCreateComposition
import shared.testUtils.BehaviorSpecUtRepo

val koinInject = MemberName("org.koin.core.component", "inject")

fun repositoryTestBuilder(dependencies: List<Class<*>>, dto: RepositoryTestBuilderDTO): FileSpec {
    val testClass = TypeSpec
        .classBuilder("CarouselOfImagesRepositoryTest")
        .superclass(
            ClassName(
                BehaviorSpecUtRepo::class.java.packageName, BehaviorSpecUtRepo::class.java.simpleName
            )
        )
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addComment("region Setup")
                .setupDependencies(dependencies)
                .signupThenCreateComposition(compositionFlow = dto.compositionFlow) // swap out
                .addStatement("beforeEach { %M() }", MemberName("io.mockk", "clearAllMocks"))
                .addComment("endregion Setup\n")

                .compComplexRepoTestBuilder(dto)
                .build()
        )
        .build()

    return FileSpec.builder("", "HelloWorld")
        .addType(testClass)
        .build()
}

fun FunSpec.Builder.setupDependencies(dependencies: List<Class<*>>): FunSpec.Builder {
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