package com.idealIntent.repositories.compositions.banners

import dtos.compositions.banners.BannerBasic
import com.idealIntent.repositories.RepositoryBase

class BasicBannerRepository : RepositoryBase() {
//    private val Database.component get() = this.sequenceOf(Spaces)

    fun createBannerBasic(component: BannerBasic): Int? {
        throw NotImplementedError()
    }

    fun deleteBannerBasic(component: BannerBasic): Int? {
        throw NotImplementedError()
    }
}