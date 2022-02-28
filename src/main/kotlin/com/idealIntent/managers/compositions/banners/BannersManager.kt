package com.idealIntent.managers.compositions.banners

import com.idealIntent.dtos.compositions.banners.BannerBasicCreateReq
import com.idealIntent.dtos.compositions.banners.BannerBasicRes
import com.idealIntent.dtos.compositions.carousels.CompositionResponse
import com.idealIntent.managers.compositions.CompositionCategoryManagerStructure
import com.idealIntent.managers.compositions.carousels.UpdateDataOfComposition
import dtos.compositions.banners.CompositionBanner
import dtos.compositions.banners.CompositionBanner.Basic

class BannersManager(
    private val bannerImageManager: BannerImageManager,
) : CompositionCategoryManagerStructure<CompositionBanner, BannerBasicRes, CompositionResponse>() {

    override fun getPublicComposition(
        compositionType: CompositionBanner,
        compositionSourceId: Int
    ): BannerBasicRes? = when (compositionType) {
        Basic -> bannerImageManager.getPublicComposition(compositionSourceId)
    }

    override fun getPrivateComposition(
        compositionType: CompositionBanner,
        compositionSourceId: Int,
        authorId: Int
    ) = when (compositionType) {
        Basic -> bannerImageManager.getPrivateComposition(compositionSourceId, authorId)
    }

    override fun createComposition(
        compositionType: CompositionBanner,
        jsonData: String,
        layoutId: Int,
        authorId: Int
    ) = when (compositionType) {
        Basic -> bannerImageManager.createComposition(
            gson.fromJson(jsonData, BannerBasicCreateReq::class.java), layoutId, authorId
        )
    }

    override fun updateComposition(
        compositionType: CompositionBanner,
        compositionSourceId: Int,
        compositionUpdateQue: List<UpdateDataOfComposition>,
        authorId: Int
    ) = when (compositionType) {
        Basic -> bannerImageManager.updateComposition(compositionUpdateQue, compositionSourceId, authorId)
    }

    override fun deleteComposition(
        compositionType: CompositionBanner,
        compositionSourceId: Int,
        authorId: Int
    ) = when (compositionType) {
        Basic -> bannerImageManager.deleteComposition(compositionSourceId, authorId)
    }
}