package com.idealIntent.other

import dtos.compositions.CompositionCategory
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

/**
 * Tests that there are no enum entries under the same value.
 *
 * Maps through all int values of enum entries and assigned to a Set collection.
 * If the length of Set collection is less than Enum then there are multiple enum
 * entries under the same value. Test will fail.
 */
class IntValueEnumsTests : BehaviorSpec({
    given("enum") {
        then("check if duplicate values of enums") {
            val setCollection: Set<String> = CompositionCategory.values().map {
                return@map CompositionCategory.fromInt(it.value).name
            }.toSet()

            setCollection.size shouldBe CompositionCategory.values().size
        }
    }
})