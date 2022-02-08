package com.idealIntent.repositories.compositions

import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesDataMapped

class CompositionDataBuilder {
    var isCarouselOfImagesData: Boolean = false
    var carouselOfImagesData = CarouselOfImagesDataMapped()
        get() {
            isCarouselOfImagesData = true
            return field
        }

    var isCarouselOfImagesData2: Boolean = false
    var carouselOfImagesData2 = CarouselOfImagesDataMapped()
        get() {
            isCarouselOfImagesData2 = true
            return field
        }

    fun getCompositionsOfLayouts() {
//        println(carouselOfImagesData)
        // serialized all that were and return
    }
}