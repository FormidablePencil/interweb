package com.idealIntent.other

import dtos.collectionsGeneric.images.ImageIdentifiableRecordByCol
import dtos.collectionsGeneric.images.ImagesCOL
import dtos.collectionsGeneric.privileges.PrivilegedAuthorCOL
import dtos.collectionsGeneric.privileges.PrivilegedAuthorIdentifiableRecordByCol
import dtos.collectionsGeneric.texts.TextsCOL
import dtos.compositions.CompositionCategory
import dtos.compositions.CompositionModLvl
import dtos.compositions.banners.CompositionBanner
import dtos.compositions.carousels.CarouselOfImagesTABLE
import dtos.compositions.carousels.CompositionCarousel
import dtos.compositions.grids.CompositionGrid
import dtos.compositions.headers.CompositionHeader
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

// todo - make this file auto generated with KotlinPoet
/**
 * Tests that there are no enum entries under the same value.
 *
 * Maps through all int values of enum entries and assigned to a Set collection.
 * If the length of Set collection is less than Enum then there are multiple enum
 * entries under the same value. Test will fail.
 */
class EnumsIntValueDuplicatesTests : BehaviorSpec({
    // region Compositions
    given("CompositionModLvl") {
        val setCollection = CompositionModLvl.values().map {
            return@map CompositionModLvl.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe CompositionModLvl.values().size
    }
    given("CompositionCategory") {
        val setCollection = CompositionCategory.values().map {
            return@map CompositionCategory.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe CompositionCategory.values().size
    }
    given("CompositionBanner") {
        val setCollection = CompositionBanner.values().map {
            return@map CompositionBanner.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe CompositionBanner.values().size
    }
    given("CompositionCarousel") {
        val setCollection = CompositionCarousel.values().map {
            return@map CompositionCarousel.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe CompositionCarousel.values().size
    }
    given("CompositionGrid") {
        val setCollection = CompositionGrid.values().map {
            return@map CompositionGrid.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe CompositionGrid.values().size
    }
    given("CompositionHeader") {
        val setCollection = CompositionHeader.values().map {
            return@map CompositionHeader.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe CompositionHeader.values().size
    }
    // endregion

    // region tables and columns for CRUD on compositions and collections
    given("CarouselOfImagesTABLE") {
        val setCollection = CarouselOfImagesTABLE.values().map {
            return@map CarouselOfImagesTABLE.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe CarouselOfImagesTABLE.values().size
    }

    given("ImageIdentifiableRecordByCol") {
        val setCollection = ImageIdentifiableRecordByCol.values().map {
            return@map ImageIdentifiableRecordByCol.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe ImageIdentifiableRecordByCol.values().size
    }
    given("ImagesCOL") {
        val setCollection = ImagesCOL.values().map {
            return@map ImagesCOL.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe ImagesCOL.values().size
    }

    given("PrivilegedAuthorCOL") {
        val setCollection = PrivilegedAuthorCOL.values().map {
            return@map PrivilegedAuthorCOL.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe PrivilegedAuthorCOL.values().size
    }
    given("PrivilegedAuthorIdentifiableRecordByCol") {
        val setCollection = PrivilegedAuthorIdentifiableRecordByCol.values().map {
            return@map PrivilegedAuthorIdentifiableRecordByCol.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe PrivilegedAuthorIdentifiableRecordByCol.values().size
    }

    given("TextsCOL") {
        val setCollection = TextsCOL.values().map {
            return@map TextsCOL.fromInt(it.value).name
        }.toSet()

        setCollection.size shouldBe TextsCOL.values().size
    }
    // endregion

    // region Other
    // endregion
})