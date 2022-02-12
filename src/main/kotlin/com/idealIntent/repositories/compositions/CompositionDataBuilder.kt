package com.idealIntent.repositories.compositions

import com.google.gson.Gson
import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesDataMapped
import dtos.compositions.CompositionCategory
import dtos.compositions.carousels.CompositionCarouselType

data class WrappedCompPresent(
    val orderRank: Int,
    val compositionCategory: Int,
    val compositionType: Int,
    val serializedComposition: String
)

class CompositionDataBuilder {
    var isCarouselOfImagesData: Boolean = false
    var carouselOfImagesData = CarouselOfImagesDataMapped()
        get() {
            isCarouselOfImagesData = true
            return field
        }

    /**
     * Wraps compositions of layout queried with composition serialized.
     *
     * @return A ready to serialize and return composition to client.
     */
    fun getCompositionsOfLayouts(): MutableList<WrappedCompPresent> {
        val gson = Gson()
        val layoutOfCompositionsWrappedPresent = mutableListOf<WrappedCompPresent>()

        when (true) {
            isCarouselOfImagesData -> {
                val comps = carouselOfImagesData.get()
                comps.forEach {
                    layoutOfCompositionsWrappedPresent += WrappedCompPresent(
                        orderRank = it.orderRank,
                        compositionType = CompositionCategory.Carousel.value,
                        compositionCategory = CompositionCarouselType.BasicImages.value,
                        serializedComposition = gson.toJson(it),
                    )
                }
            }
        }

        // todo -
        //  order rank is always 0,
        //  seems there's a left join implemented improperly for privileged authors

        return layoutOfCompositionsWrappedPresent
    }
}