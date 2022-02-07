package com.idealIntent.repositories.compositions

import com.idealIntent.repositories.compositions.carousels.CarouselOfImagesData

class CompositionBuilder {
    var isCarouselOfImagesData: Boolean = false
    var carouselOfImagesData = CarouselOfImagesData()
        set(value) {
            isCarouselOfImagesData = true
            field = value
        }

    fun getCompositionsOfLayouts() {
        // serialized all that were and return
    }
}