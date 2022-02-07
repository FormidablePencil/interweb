package com.idealIntent.repositories.compositions

import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesData

class CompositionBuilder {
    var isCarouselOfImagesData: Boolean = false
    var carouselOfImagesData = CarouselOfImagesData()
        get() {
            isCarouselOfImagesData = true
            return field
        }

    var isCarouselOfImagesData2: Boolean = false
    var carouselOfImagesData2 = CarouselOfImagesData()
        get() {
            isCarouselOfImagesData2 = true
            return field
        }

    fun getCompositionsOfLayouts() {
//        println(carouselOfImagesData)
        // serialized all that were and return
    }
}