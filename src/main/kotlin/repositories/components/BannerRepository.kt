package repositories.components

import dtos.libOfComps.banners.BannerBasic
import repositories.RepositoryBase

class BannerRepository : RepositoryBase() {
//    private val Database.component get() = this.sequenceOf(Spaces)

    fun createBannerBasic(component: BannerBasic): Int? {
        throw NotImplementedError()
    }

    fun deleteBannerBasic(component: BannerBasic): Int? {
        throw NotImplementedError()
    }
}