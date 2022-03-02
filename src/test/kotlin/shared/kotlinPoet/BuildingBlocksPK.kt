package shared.kotlinPoet

import com.squareup.kotlinpoet.FunSpec

//    val f = TypeSpec.Builder().signupThenCreateComposition
object TestBuilderKP {
    fun FunSpec.Builder.given(name: String, map: Map<String, Any?>, code: () -> String): FunSpec.Builder {
        return this
            .addCode("given(\"$name\") {⇥")
            .addNamedCode(code(), map)
            .addCode("⇤\n}\n")
    }

    fun and(name: String, code: () -> String): String {
        return "⇥and(\"$name\") {${code()}}⇥\n"
    }

    fun then(name: String, code: () -> String): String {
        return "\n\nthen(\"$name\") {⇥\nrollback {⇥\n${code()}\n⇤}\n⇤}"
    }
}

fun main() {
}