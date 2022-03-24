package com.idealIntent.repositories.compositions.banners

import com.idealIntent.dtos.collectionsGeneric.privileges.PrivilegedAuthor
import com.idealIntent.dtos.compositions.banners.BannerImageRes
import com.idealIntent.exceptions.CompositionCode.PrivilegeForCompNotFound
import com.idealIntent.exceptions.CompositionExceptionReport
import com.idealIntent.repositories.compositions.headers.IDataMapper

/**
 * Used to map all data from sql query and then format it with [get]
 *
 * @property data List of composition id and composition itself
 * @property privilegedAuthorsOfComps List of privileged authors belonging to what component
 * List<Pair<id of comp, list of privileged authors of comp>>
 */
class BannerImageDataMapped : IDataMapper<BannerImageRes> {
    val data: MutableList<Pair<Int, BannerImageRes>> = mutableListOf()
    val privilegedAuthorsOfComps: MutableList<Pair<Int, MutableList<PrivilegedAuthor>>> = mutableListOf()

    override fun get(): List<BannerImageRes> {
        return data.map {
            val privilegedAuthors = privilegedAuthorsOfComps.find { item ->
                item.first == it.first
            }?.second ?: throw CompositionExceptionReport(PrivilegeForCompNotFound, this::class.java)

            BannerImageRes(
                compositionId = it.second.compositionId,
                sourceId = it.second.sourceId,
                name = it.second.name,
                privilegeLevel = it.second.privilegeLevel,
                privilegedAuthors = privilegedAuthors,
                imageUrl = it.second.imageUrl,
                imageAlt = it.second.imageAlt,
            )
        }
    }
}