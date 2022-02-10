package com.idealIntent.managers.compositions.carousels

enum class UpdateDataOfCarouselOfImages(val value: Int) {
    Image(0),
    RedirectText(1),
//    PrivilegedAuthor(2), todo separate action
    CompositionName(3);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}