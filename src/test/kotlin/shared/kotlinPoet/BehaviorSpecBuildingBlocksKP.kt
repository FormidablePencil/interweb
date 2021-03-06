package shared.kotlinPoet

import com.squareup.kotlinpoet.FunSpec

object BehaviorSpecBuildingBlocksKP {
    fun FunSpec.Builder.given(name: String, map: Map<String, Any?>, code: () -> String): FunSpec.Builder {
        return this
            .addCode("\ngiven(\"$name\") {⇥")
            .addNamedCode(code(), map)
            .addCode("⇤\n}\n")
    }

    fun and(name: String, code: () -> String): String {
        return "⇥and(\"$name\") {${code()}}⇥\n"
    }

    fun then(name: String, code: () -> String): String {
        return "\n\nthen(\"$name\") {⇥\n%rollback:M {⇥\n${code()}\n⇤}\n⇤}"
    }
}